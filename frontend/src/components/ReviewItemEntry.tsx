import React from "react";

type ReviewItemEntryProps = {
  name: string;
  count: number;
  index: number;
  style?: React.CSSProperties;
};

function ReviewItemEntry({ name, count, index, style }: ReviewItemEntryProps) {
  return (
    <li
      className={"list-group-item " + (index % 2 === 1 ? "bg-body-secondary" : "")}
      style={style}
    >
      <div className="d-flex justify-content-between">
        <span className={"my-auto"}>{name}</span>
        <div className="d-flex">
          x<span className={"my-auto mx-3 fw-bolder"}>{count}</span>
        </div>
      </div>
    </li>
  );
}

export default ReviewItemEntry;
