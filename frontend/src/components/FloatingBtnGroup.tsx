import React from "react";

type ButtonProps = {
  label: React.ReactNode;
  style: "primary" | "secondary" | "success";
  onClick: () => void;
};

type FloatingBtnGroupProps = {
  buttons: ButtonProps[];
};

function FloatingBtnGroup({ buttons }: FloatingBtnGroupProps) {
  return (
    <div className={"position-fixed bottom-0 end-0 d-flex"}>
      <div className={"btn-group d-flex ms-auto mb-3 me-3"}>
        {buttons.map((button, index) => (
          <button
            key={index}
            className={`btn btn-${button.style}`}
            onClick={button.onClick}
          >
            {button.label}
          </button>
        ))}
      </div>
    </div>
  );
}

export default FloatingBtnGroup;
