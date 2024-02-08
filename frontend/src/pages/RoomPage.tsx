import { BsDash, BsPlus } from "react-icons/bs";
import React, { useMemo, useRef, useState } from "react";
import { useWindowVirtualizer } from "@tanstack/react-virtual";

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

  const filteredItems = useMemo(() => {
    return items.filter((item) => counts.get(item) ?? 0 > 0);
  }, [items, counts]);

  return (
    <>
      <div className={"container-fluid position-relative py-3"}>
        <h1>{review ? "Order review" : "Room"}</h1>
        {review && filteredItems.length === 0 ? (
          <div className={"alert alert-info"} role="alert">
            You have not ordered anything yet.
          </div>
        ) : (
          <VirtualizedList
            items={review ? filteredItems : items}
            counts={counts}
            setCounts={setCounts}
          />
        )}
        <div className={"position-fixed bottom-0 end-0 d-flex btn-group"}>
          {review && <button className={"btn btn-primary mb-3"}>See room total</button>}
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
    </>
  );
}

type VirtualizedListProps = {
  items: string[];
  counts: Map<string, number>;
  setCounts: (newCounts: Map<string, number>) => void;
};

function VirtualizedList({ items, counts, setCounts }: VirtualizedListProps) {
  const listRef = useRef<HTMLDivElement>(null);
  const virtualizer = useWindowVirtualizer({
    count: items.length,
    estimateSize: () => 48,
    overscan: 50,
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
                setCounts((old) => {
                  const newCounts = new Map(old);
                  newCounts.set(item, (old.get(item) ?? 0) + 1);
                  return newCounts;
                });
              }}
              onDecrement={() => {
                setCounts((old) => {
                  const newCounts = new Map(old);
                  newCounts.set(item, Math.max((old.get(item) ?? 0) - 1, 0));
                  return newCounts;
                });
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
