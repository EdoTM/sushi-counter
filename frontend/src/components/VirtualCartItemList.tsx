import CartItemEntry from "./CartItemEntry.tsx";
import VirtualItemList from "./VirtualItemList.tsx";

type VirtualCartItemListProps = {
  items: string[];
  counts: Map<string, number>;
  onCountChange: (item: string, count: number) => void;
};

function VirtualCartItemList({
  items,
  counts,
  onCountChange,
}: VirtualCartItemListProps) {
  return (
    <VirtualItemList
      items={items}
      renderItem={(item, index, style) => (
        <CartItemEntry
          key={index}
          name={item}
          count={counts.get(item) ?? 0}
          onIncrement={() => {
            onCountChange(item, (counts.get(item) ?? 0) + 1);
          }}
          onDecrement={() => {
            onCountChange(item, counts.get(item)! - 1);
          }}
          style={style}
        />
      )}
      estimateSize={48}
    />
  );
}

export default VirtualCartItemList;
