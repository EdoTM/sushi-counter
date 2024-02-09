import VirtualCartItemList from "../../components/VirtualCartItemList.tsx";
import FloatingBtnGroup from "../../components/FloatingBtnGroup.tsx";
import { useState } from "react";

type MakeOrdersViewProps = {
  items: string[];
  counts: Map<string, number>;
  handleCountChange: (item: string, count: number) => void;
  onReviewClick: () => void;
  onAddItem: (item: string) => void;
};

function MakeOrdersView({
  handleCountChange,
  counts,
  items,
  onReviewClick,
  onAddItem,
}: MakeOrdersViewProps) {
  const [customItem, setCustomItem] = useState("");

  return (
    <>
      <div className={"input-group my-3"}>
        <input
          type="text"
          className={"form-control"}
          value={customItem}
          onChange={(e) => setCustomItem(e.currentTarget.value)}
          placeholder={"Custom item"}
        />
        <button
          className={"btn btn-secondary"}
          onClick={() => setEditCustomItem(false)}
        >
          Cancel
        </button>
        <button
          className={"btn btn-primary"}
          onClick={() => onAddItem(customItem)}
        >
          Add
        </button>
      </div>
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
