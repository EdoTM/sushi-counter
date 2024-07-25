import FloatingBtnGroup from "../../components/FloatingBtnGroup.tsx";
import { useEffect, useMemo, useState } from "react";
import VirtualReviewItemList from "../../components/VirtualReviewItemList.tsx";
import { getRoomTotalOrders } from "../../api.ts";

type RoomTotalsViewProps = {
  allItems: string[];
  addNewItems: (newItems: string[]) => void;
  onGoBackClick: () => void;
};

function RoomTotalsView({
  allItems,
  onGoBackClick,
  addNewItems,
}: RoomTotalsViewProps) {
  const [roomCounts, setRoomCounts] = useState<Map<string, number>>(new Map());

  useEffect(() => {
    getRoomTotalOrders().then((res) => {
      const newRoomCounts = new Map<string, number>();
      const newItems: string[] = [];
      for (const [item, count] of Object.entries(res.data)) {
        newRoomCounts.set(item, count);
        if (!allItems.includes(item)) {
          newItems.push(item);
        }
      }
      if (newItems.length > 0) {
        addNewItems(newItems);
      }
      setRoomCounts(newRoomCounts);
    });
  }, []);

  const filteredItems = useMemo(() => {
    return allItems.filter((item) => roomCounts.get(item) ?? 0 > 0);
  }, [allItems, roomCounts]);
  return (
    <>
      <VirtualReviewItemList items={filteredItems} counts={roomCounts} />
      <FloatingBtnGroup
        buttons={[
          {
            label: "Go Back",
            onClick: onGoBackClick,
            style: "secondary",
          },
        ]}
      />
    </>
  );
}

export default RoomTotalsView;
