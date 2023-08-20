package components;

import jade.Camera;
import jade.GameObject;
import jade.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2D;
import physics2d.components.Rigidbody2D;

public class EnemyFireball extends Component {
    public transient boolean goingRight = false;
    public transient boolean goingLeft = false;
    public transient boolean goingUp = false;
    public transient boolean goingDown = false;
    private transient Rigidbody2D rb;
    private transient float fireballSpeed = 1.7f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);
    private transient boolean onGround = false;
    private transient float lifetime = 4.0f;

    private static int fireballCount = 0;

    public static boolean canSpawn() {
        return fireballCount < 4;
    }

    @Override
    public void start() {
        this.rb = this.gameObject.getComponent(Rigidbody2D.class);
        this.rb.setGravityScale(0.0f);
//        this.acceleration.y = 0;
        fireballCount++;
    }

    @Override
    public void update(float dt) {
        lifetime -= dt;
        if (lifetime <= 0) {
            disappear();
            return;
        }

        if (goingRight) {
            velocity.x = fireballSpeed;
        } else if (goingLeft) {
            velocity.x = -fireballSpeed;
        } else if (goingUp) {
            velocity.y = fireballSpeed;
        } else if (goingDown) {
            velocity.y = -fireballSpeed;
        }

        this.rb.setVelocity(velocity);
    }

    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (goingUp && contactNormal.y == 1.0f) {
            disappear();
        } else if (goingDown && contactNormal.y == -1.0f) {
            disappear();
        }
        if (goingRight && contactNormal.x == 1.0f) {
            disappear();
        } else if (goingLeft && contactNormal.x == -1.0f) {
            disappear();
        }
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (obj.getComponent(PlayerController.class) != null ||
            obj.getComponent(Fireball.class) != null) {
            contact.setEnabled(false);
        }
    }

    public void disappear() {
        fireballCount--;
        this.gameObject.destroy();
    }
}
