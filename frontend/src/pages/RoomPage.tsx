import { BsExclamationTriangleFill } from "react-icons/bs";
import { useEffect, useMemo, useState } from "react";
import { connectToRoomWebSocket, getOrders } from "../api.ts";
import { Link } from "react-router-dom";
import VirtualCartItemList from "../components/VirtualCartItemList.tsx";

function getInitialItems(max: number): string[] {
  const items = [];
  for (let i = 1; i <= max; i++) {
    items.push(`${i}`);
  }
  return items;
}

function RoomPage() {
  const [items, _] = useState<string[]>(getInitialItems(200));
  const [counts, setCounts] = useState<Map<string, number>>(new Map());
  const [review, setReview] = useState<boolean>(false);
  const [ws, setWs] = useState<WebSocket>();
  const [disconnected, setDisconnected] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);

  const filteredItems = useMemo(() => {
    return items.filter((item) => counts.get(item) ?? 0 > 0);
  }, [items, counts]);

  useEffect(() => {
    setLoading(true);
    getOrders().then((res) => {
      const newCounts = new Map<string, number>();
      for (const [item, count] of Object.entries(res.data)) {
        newCounts.set(item, count);
      }
      setCounts(newCounts);
      setLoading(false);
    });
    const ws = connectToRoomWebSocket();
    ws.onclose = (e) => {
      console.log("Socket closed", e);
      setDisconnected(true);
    };
    ws.onerror = (e) => {
      console.log("Socket error", e);
      setDisconnected(true);
    };
    setWs(ws);
  }, []);

  const handleCountChange = (item: string, count: number) => {
    if (ws?.readyState === WebSocket.OPEN) {
      const newCounts = new Map(counts);
      if (count === 0) {
        newCounts.delete(item);
      } else {
        newCounts.set(item, count);
      }
      console.log(`Sending - order:${item}/${count}`);
      ws.send(`order:${item}/${count}`);
      setCounts(newCounts);
    }
  };

  return (
    <>
      <div className={"container-fluid position-relative py-3"}>
        <h1>{review ? "Order review" : "Room"}</h1>
        {loading ? (
          <div className={"d-flex"}>
            <div className={"mx-auto spinner spinner-border mt-5"} />
          </div>
        ) : review && filteredItems.length === 0 ? (
          <div className={"alert alert-info"} role="alert">
            You have not ordered anything yet.
          </div>
        ) : (
          <VirtualCartItemList
            items={review ? filteredItems : items}
            counts={counts}
            onCountChange={handleCountChange}
          />
        )}
        <div className={"position-fixed bottom-0 end-0 d-flex btn-group"}>
          {review && (
            <button className={"btn btn-primary mb-3"}>See room total</button>
          )}
          <button
            className={
              "btn ms-auto mb-3 me-3 btn-" + (review ? "secondary" : "primary")
            }
            onClick={() => setReview((r) => !r)}
          >
            {review ? "Go back" : "Review"}
          </button>
        </div>
      </div>
      {disconnected && (
        <div className={"position-fixed d-flex top-0 w-100"}>
          <div
            className={
              "alert alert-danger mx-auto mt-3 d-flex align-items-center gap-2"
            }
          >
            <BsExclamationTriangleFill /> Disconnected from room.{" "}
            <Link className={"link-light"} to={"/"}>
              Exit
            </Link>
          </div>
        </div>
      )}
    </>
  );
}

export default RoomPage;
