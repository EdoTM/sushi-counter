import { BsExclamationTriangleFill } from "react-icons/bs";
import { Link } from "react-router-dom";

function DisconnectedAlert() {
  return (
    <div className={"position-fixed d-flex top-0 w-100"}>
      <div
        className={
          "alert alert-danger mx-auto mt-3 d-flex align-items-center gap-2"
        }
      >
        <BsExclamationTriangleFill /> Disconnected from room.{" "}
        <Link className={"link-light"} to={"/"}>
          Exit
        </Link>
      </div>
    </div>
  );
}

export default DisconnectedAlert;