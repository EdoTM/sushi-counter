import { useState } from "react";

function JoinRoomPage() {
  const [roomCode, setRoomCode] = useState<string>();

  return (
    <div className={"container-fluid pt-5"}>
      <input
        type="text"
        className={"form-control"}
        value={roomCode}
        placeholder={"Enter Room Code"}
        maxLength={20}
        onInput={(e) => setRoomCode(e.currentTarget.value)}
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
