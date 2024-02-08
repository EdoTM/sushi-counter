import React, { useRef } from "react";
import { useWindowVirtualizer } from "@tanstack/react-virtual";

type VirtualItemListProps = {
  items: string[];
  renderItem: (
    item: string,
    index: number,
    style: React.CSSProperties,
  ) => OneOrMore<React.ReactNode>;
};

function VirtualItemList({ items, renderItem }: VirtualItemListProps) {
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
          return renderItem(item, index, {
            top: 0,
            left: 0,
            width: "100%",
            transform: `translateY(${
              virtualItem.start - virtualizer.options.scrollMargin
            }px)`,
            position: "absolute",
          });
        })}
      </div>
    </div>
  );
}

export default VirtualItemList;
