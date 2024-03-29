import { useState } from "react";
import { createRoom, joinRoom } from "../api.ts";
import { useNavigate } from "react-router-dom";

function HomePage() {
  const [roomName, setRoomName] = useState<string>("");
  const navigate = useNavigate();

  return (
    <div className={"container-fluid pt-5"}>
      <h1 className={"app-title-test"}>Sushi counter</h1>

      <input
        type="text"
        className={"form-control"}
        value={roomName}
        placeholder={"Enter Room Name"}
        maxLength={20}
        onInput={(e) => setRoomName(e.currentTarget.value)}
      />

      <div
        className={"d-flex flex-column flex-sm-row justify-content-center mt-3"}
      >
        <button
          className={"btn btn-primary"}
          onClick={async () => {
            const res = await createRoom(roomName);
            if (res.status === 200) {
              const joinRes = await joinRoom(roomName);
              if (joinRes.status === 200) {
                navigate(`/room`);
              } else {
                alert("Error joining room");
                return;
              }
            } else if (res.status !== 201) {
              alert("Error creating room");
              return;
            }
            navigate(`/room`);
          }}
        >
          Create or join
        </button>
      </div>
    </div>
  );
}

export default HomePage;
