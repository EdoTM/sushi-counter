import { BsDash, BsExclamationTriangleFill, BsPlus } from "react-icons/bs";
import React, { useEffect, useMemo, useRef, useState } from "react";
import { useWindowVirtualizer } from "@tanstack/react-virtual";
import { connectToRoomWebSocket, getOrders } from "../api.ts";
import { Link } from "react-router-dom";

function getInitialItems(max: number): string[] {
  const items = [];
  for (let i = 1; i <= max; i++) {
    items.push(`${i}`);
  }
  return items;
}

function RoomPage() {
  const [items, setItems] = useState<string[]>(getInitialItems(200));
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
          <VirtualizedItemList
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

type VirtualizedListProps = {
  items: string[];
  counts: Map<string, number>;
  onCountChange: (item: string, count: number) => void;
};

function VirtualizedItemList({
  items,
  counts,
  onCountChange,
}: VirtualizedListProps) {
  const listRef = useRef<HTMLDivElement>(null);
  const virtualizer = useWindowVirtualizer({
    count: items.length,
    estimateSize: () => 48,
    overscan: 20,
    scrollMargin: listRef.current?.offsetTop ?? 0,
  });

  return (
    <div className="card" ref={listRef}>
      <div
        className="list-group list-group-flush"
        style={{
          height: `${virtualizer.getTotalSize()}px`,
          width: "100%",
          position: "relative",
        }}
      >
        {virtualizer.getVirtualItems().map((virtualItem) => {
          const { index } = virtualItem;
          const item = items[index];
          return (
            <ItemCardEntry
              key={index}
              name={item}
              count={counts.get(item) ?? 0}
              onIncrement={() => {
                onCountChange(item, (counts.get(item) ?? 0) + 1);
              }}
              onDecrement={() => {
                onCountChange(item, counts.get(item)! - 1);
              }}
              style={{
                top: 0,
                left: 0,
                width: "100%",
                transform: `translateY(${
                  virtualItem.start - virtualizer.options.scrollMargin
                }px)`,
                position: "absolute",
              }}
            />
          );
        })}
      </div>
    </div>
  );
}

type ItemCardEntryProps = {
  name: string;
  count: number;
  onIncrement: () => void;
  onDecrement: () => void;
  style?: React.CSSProperties;
};

function ItemCardEntry({
  name,
  count,
  onIncrement,
  onDecrement,
  style,
}: ItemCardEntryProps) {
  return (
    <li
      className={"list-group-item " + (count > 0 ? "bg-body-secondary" : "")}
      style={style}
    >
      <div className="d-flex justify-content-between">
        <span className={"my-auto"}>{name}</span>
        <div className="d-flex">
          <button
            className="btn btn-outline-secondary btn-sm"
            onClick={onDecrement}
            disabled={count === 0}
          >
            <BsDash />
          </button>
          <span className={"my-auto mx-3"}>{count}</span>
          <button
            className="btn btn-outline-secondary btn-sm"
            onClick={onIncrement}
          >
            <BsPlus />
          </button>
        </div>
      </div>
    </li>
  );
}

export default RoomPage;
