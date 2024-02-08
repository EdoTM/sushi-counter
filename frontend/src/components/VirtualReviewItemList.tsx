import VirtualItemList from "./VirtualItemList.tsx";
import ReviewItemEntry from "./ReviewItemEntry.tsx";

type VirtualReviewItemListProps = {
  items: string[];
  counts: Map<string, number>;
};

function VirtualReviewItemList({ items, counts }: VirtualReviewItemListProps) {
  return (
    <VirtualItemList
      items={items}
      estimateSize={40}
      renderItem={(item, index, style) => (
        <ReviewItemEntry
          key={index}
          name={item}
          index={index}
          count={counts.get(item) ?? 0}
          style={style}
        />
      )}
    />
  );
}

export default VirtualReviewItemList;
