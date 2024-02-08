import { BsExclamationTriangleFill } from "react-icons/bs";
import { useEffect, useMemo, useState } from "react";
import {
  connectToRoomWebSocket,
  getOrders,
  getRoomTotalOrders,
} from "../api.ts";
import { Link } from "react-router-dom";
import VirtualCartItemList from "../components/VirtualCartItemList.tsx";
import VirtualReviewItemList from "../components/VirtualReviewItemList.tsx";

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
  const [reviewRoomTotal, setReviewRoomTotal] = useState<boolean>(false);
  const [roomCounts, setRoomCounts] = useState<Map<string, number>>(new Map());

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

  const getRoomTotal = () => {
    getRoomTotalOrders().then((res) => {
      const newRoomCounts = new Map<string, number>();
      for (const [item, count] of Object.entries(res.data)) {
        newRoomCounts.set(item, count);
      }
      setRoomCounts(newRoomCounts);
    });
  };

  const itemsList = () => {
    if (loading) {
      return (
        <div className={"d-flex"}>
          <div className={"mx-auto spinner spinner-border mt-5"} />
        </div>
      );
    } else if (review && filteredItems.length === 0) {
      return (
        <div className={"alert alert-info"} role="alert">
          You have not ordered anything yet.
        </div>
      );
    } else if (reviewRoomTotal) {
      const totalFilteredItems = items.filter((item) => roomCounts.get(item) ?? 0 > 0);
      return <VirtualReviewItemList items={totalFilteredItems} counts={roomCounts} />;
    }
    return (
      <VirtualCartItemList
        items={review ? filteredItems : items}
        counts={counts}
        onCountChange={handleCountChange}
      />
    );
  };

  return (
    <>
      <div className={"container-fluid position-relative py-3"}>
        <h1>{review ? "Order review" : "Room"}</h1>
        {itemsList()}
        <div className={"position-fixed bottom-0 end-0 d-flex btn-group"}>
          {review && !reviewRoomTotal && (
            <button
              className={"btn btn-primary mb-3"}
              onClick={() => {
                getRoomTotal();
                setReviewRoomTotal(true);
              }}
            >
              See room total
            </button>
          )}
          <button
            className={
              "btn ms-auto mb-3 me-3 btn-" + (review ? "secondary" : "primary")
            }
            onClick={() => {
              setReview((r) => !r)
              setReviewRoomTotal(false);
            }}
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
