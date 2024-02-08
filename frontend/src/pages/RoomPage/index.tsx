import { BsExclamationTriangleFill } from "react-icons/bs";
import { useEffect, useState } from "react";
import { connectToRoomWebSocket, getOrders } from "../../api.ts";
import { Link } from "react-router-dom";
import ReviewOrdersView from "./ReviewOrdersView.tsx";
import RoomTotalsView from "./RoomTotalsView.tsx";
import MakeOrdersView from "./MakeOrdersView.tsx";
import DisconnectedAlert from "../../components/DisconnectedAlert.tsx";

function getInitialItems(max: number): string[] {
  const items = [];
  for (let i = 1; i <= max; i++) {
    items.push(`${i}`);
  }
  return items;
}

type Page = "make" | "review" | "roomTotals";

const pageTitle = {
  make: "Make Orders",
  review: "Review Orders",
  roomTotals: "Room Totals",
}

function RoomPage() {
  const [items, _] = useState<string[]>(getInitialItems(300));
  const [counts, setCounts] = useState<Map<string, number>>(new Map());
  const [ws, setWs] = useState<WebSocket>();
  const [disconnected, setDisconnected] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);
  const [page, setPage] = useState<Page>("make");

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

  const pageView = () => {
    switch (page) {
      case "review":
        return (
          <ReviewOrdersView
            handleCountChange={handleCountChange}
            counts={counts}
            allItems={items}
            onGoBackClick={() => setPage("make")}
            onSeeRoomTotalClick={() => setPage("roomTotals")}
          />
        );
      case "roomTotals":
        return (
          <RoomTotalsView
            allItems={items}
            onGoBackClick={() => setPage("review")}
          />
        );
      default:
        return (
          <MakeOrdersView
            handleCountChange={handleCountChange}
            counts={counts}
            items={items}
            onReviewClick={() => setPage("review")}
          />
        );
    }
  };

  return (
    <>
      <div className={"container-fluid position-relative py-3"}>
        <h1>{pageTitle[page]}</h1>
        {loading ? <p>Loading...</p> : pageView()}
        {disconnected && <DisconnectedAlert />}
      </div>
    </>
  );
}

export default RoomPage;
