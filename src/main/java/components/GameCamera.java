package components;

import jade.Camera;
import jade.GameObject;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class GameCamera extends Component {
    private transient GameObject player;
    private transient Camera gameCamera;
    private transient float highestX = Float.MIN_VALUE;
    private transient float highestY = Float.MAX_VALUE;
    private transient float undergroundYLevel = 0.0f;
    private transient float cameraBuffer = 1.5f;
    private transient float playerBuffer = 0.25f;

    private Vector4f skyColor = new Vector4f(0, 0, 0,1);
    private Vector4f undergroundColor = new Vector4f(0, 0, 0, 1);

    public GameCamera(Camera gameCamera) {
        this.gameCamera = gameCamera;
    }

    @Override
    public void start() {
        this.player = Window.getScene().getGameObjectWith(PlayerController.class);
        this.gameCamera.clearColor.set(skyColor);
    }

    @Override
    public void update(float dt) {
        gameCamera.position.x = player.transform.position.x - cameraBuffer;
        gameCamera.position.y = player.transform.position.y - cameraBuffer;
    }
}
