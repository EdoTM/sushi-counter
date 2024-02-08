import React from "react";
import { BsDash, BsPlus } from "react-icons/bs";

type CartItemEntryProps = {
  name: string;
  count: number;
  onIncrement: () => void;
  onDecrement: () => void;
  style?: React.CSSProperties;
};

function CartItemEntry({
  name,
  count,
  onIncrement,
  onDecrement,
  style,
}: CartItemEntryProps) {
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

export default CartItemEntry;