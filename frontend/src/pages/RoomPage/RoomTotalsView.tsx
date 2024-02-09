import FloatingBtnGroup from "../../components/FloatingBtnGroup.tsx";
import { useEffect, useMemo, useState } from "react";
import VirtualReviewItemList from "../../components/VirtualReviewItemList.tsx";
import { getRoomTotalOrders } from "../../api.ts";

type RoomTotalsViewProps = {
  allItems: string[];
  onGoBackClick: () => void;
};

function RoomTotalsView({ allItems, onGoBackClick }: RoomTotalsViewProps) {
  const [roomCounts, setRoomCounts] = useState<Map<string, number>>(new Map());

  useEffect(() => {
    getRoomTotalOrders().then((res) => {
      const newRoomCounts = new Map<string, number>();
      for (const [item, count] of Object.entries(res.data)) {
        newRoomCounts.set(item, count);
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
