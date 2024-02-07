import { useState } from "react";
import {BsPlus} from "react-icons/bs";

function JoinRoomPage() {
  const [roomCode, setRoomCode] = useState<string>();

  return (
    <div className={"container-fluid pt-5"}>
      <h2 className={"text-center"}>Join Room</h2>
      <div className={"input-group"}>
        <input
          type="text"
          className={"form-control"}
          value={roomCode}
          onInput={(e) => setRoomCode(e.currentTarget.value)}
        />
        <button className={"btn btn-primary"} onClick={() => {}}>
          Join Room
        </button>
      </div>

      <h3 className={"text-center my-4"}>or</h3>
      <button
        className={"btn btn-success btn-lg d-flex lh-1 align-items-center mx-auto"}
        onClick={() => {}}
      >
        <BsPlus size={25} /> Create Room
      </button>
    </div>
  );
}

export default JoinRoomPage;
