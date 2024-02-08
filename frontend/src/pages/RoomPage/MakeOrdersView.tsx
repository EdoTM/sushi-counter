import VirtualCartItemList from "../../components/VirtualCartItemList.tsx";
import FloatingBtnGroup from "../../components/FloatingBtnGroup.tsx";

type MakeOrdersViewProps = {
  items: string[];
  counts: Map<string, number>;
  handleCountChange: (item: string, count: number) => void;
  onReviewClick: () => void;
};

function MakeOrdersView({
  handleCountChange,
  counts,
  items,
  onReviewClick
}: MakeOrdersViewProps) {
  return (
    <>
      <VirtualCartItemList
        items={items}
        counts={counts}
        onCountChange={handleCountChange}
      />
      <FloatingBtnGroup
        buttons={[
          {
            label: "Review",
            onClick: onReviewClick,
            style: "primary",
          },
        ]}
      />
    </>
  );
}

export default MakeOrdersView;
