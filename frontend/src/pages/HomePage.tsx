import { useState } from "react";
import {createRoom, joinRoom} from "../api.ts";

function HomePage() {
  const [roomName, setRoomName] = useState<string>("");

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

      <div
        className={
          "d-flex flex-column flex-sm-row justify-content-center mt-3"
        }
      >
        <button className={"btn btn-primary"} onClick={async () => {
          const res = await createRoom(roomName);
          if (res.status === 200) {
            const joinRes = await joinRoom(roomName);
            if (joinRes.status === 200) {
              window.location.href = `/room`;
            } else {
              alert("Error joining room");
              return;
            }
          } else if (res.status !== 201) {
            alert("Error creating room");
            return;
          }
          window.location.href = `/room`;
        }}>
          Create or join
        </button>
      </div>
    </div>
  );
}

export default HomePage;
