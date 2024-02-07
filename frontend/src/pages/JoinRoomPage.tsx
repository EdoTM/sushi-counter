import { useState } from "react";

function JoinRoomPage() {
  const [roomName, setRoomName] = useState<string>();

  return (
    <div className={"container-fluid pt-5"}>
      <input
        type="text"
        className={"form-control"}
        value={roomName}
        placeholder={"Enter Room Name"}
        maxLength={20}
        onInput={(e) => setRoomName(e.currentTarget.value)}
      />

      <div className={"container-md d-flex flex-column flex-md-row justify-content-center mt-3"}>
        <button
          className={
            "btn btn-primary"
          }
          onClick={() => {
          }}
        >
          Join
        </button>
        <span className={"mx-auto my-2"}>or</span>
        <button
          className={
            "btn btn-secondary"
          }
          onClick={() => {
          }}
        >
          Create
        </button>
      </div>
    </div>
  );
}

export default JoinRoomPage;
