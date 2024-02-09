import FloatingBtnGroup from "../../components/FloatingBtnGroup.tsx";
import VirtualCartItemList from "../../components/VirtualCartItemList.tsx";
import { useMemo } from "react";

type ReviewOrdersViewProps = {
  handleCountChange: (item: string, count: number) => void;
  allItems: string[];
  counts: Map<string, number>;
  onSeeRoomTotalClick: () => void;
  onGoBackClick: () => void;
};

function ReviewOrdersView({
  handleCountChange,
  counts,
  allItems,
  onGoBackClick,
  onSeeRoomTotalClick,
}: ReviewOrdersViewProps) {
  const filteredItems = useMemo(() => {
    return allItems.filter((item) => counts.get(item) ?? 0 > 0);
  }, [allItems, counts]);
  return (
    <>
      {filteredItems.length > 0 ? (
        <VirtualCartItemList
          items={filteredItems}
          counts={counts}
          onCountChange={handleCountChange}
        />
      ) : (
        <p>No items selected.</p>
      )}
      <FloatingBtnGroup
        buttons={[
          {
            label: "See room total",
            onClick: onSeeRoomTotalClick,
            style: "primary",
          },
          {
            label: "Go back",
            onClick: onGoBackClick,
            style: "secondary",
          },
        ]}
      />
    </>
  );
}

export default ReviewOrdersView;
