package components;

import jade.Camera;
import jade.GameObject;
import jade.Prefabs;
import jade.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2d.Physics2D;
import physics2d.components.Rigidbody2D;
import util.AssetPool;

import static java.lang.Math.abs;

public class GoombaAI extends Component {

    private transient GameObject player;

    private transient boolean goingRight = false;
    private transient Rigidbody2D rb;
    private transient float walkSpeed = 0.6f;
    private transient Vector2f velocity = new Vector2f();
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f terminalVelocity = new Vector2f();
    private transient boolean onGround = false;
    private transient boolean isDead = false;
    private transient float timeToKill = 0.5f;
    private transient StateMachine stateMachine;

    @Override
    public void start() {
        this.player = Window.getScene().getGameObjectWith(PlayerController.class);
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rb = gameObject.getComponent(Rigidbody2D.class);
        this.rb.setGravityScale(0.0f);
    }

    int i = 0;

    @Override
    public void update(float dt) {
        Camera camera = Window.getScene().camera();
        if (this.gameObject.transform.position.x >
                camera.position.x + camera.getProjectionSize().x * camera.getZoom()) {
            return;
        }

        if (isDead) {
            timeToKill -= dt;
            if (timeToKill <= 0) {
                this.gameObject.destroy();
            }
            this.rb.setVelocity(new Vector2f());
            return;
        }

        if (goingRight) {
            velocity.x = walkSpeed;
        } else {
            velocity.x = -walkSpeed;
        }

        float xOffset = this.gameObject.transform.position.x - this.player.transform.position.x;
        float yOffset = this.gameObject.transform.position.y - this.player.transform.position.y;

        if (i % 100 == 0) {
            if (abs(xOffset) > abs(yOffset) || abs(xOffset) == abs(yOffset)) {
                if (xOffset < 0) {
                    Vector2f position = new Vector2f(this.gameObject.transform.position)
                            .add(this.gameObject.transform.scale.x > 0
                                    ? new Vector2f(0.26f, 0)
                                    : new Vector2f(-0.26f, 0));

                    GameObject fireball = Prefabs.generateEnemyFireball(position);
                    fireball.getComponent(EnemyFireball.class).goingRight =
                            this.gameObject.transform.scale.x > 0;
                    Window.getScene().addGameObjectToScene(fireball);
                } else if (xOffset > 0) {
                    Vector2f position = new Vector2f(this.gameObject.transform.position)
                            .add(this.gameObject.transform.scale.x > 0
                                    ? new Vector2f(-0.26f, 0)
                                    : new Vector2f(0.26f, 0));

                    GameObject fireball = Prefabs.generateEnemyFireball(position);
                    fireball.getComponent(EnemyFireball.class).goingLeft =
                            this.gameObject.transform.scale.x > 0;
                    Window.getScene().addGameObjectToScene(fireball);
                }
            } else if (abs(yOffset) > abs(xOffset)) {
                if (yOffset < 0) {
                    Vector2f position = new Vector2f(this.gameObject.transform.position)
                            .add(this.gameObject.transform.scale.y > 0
                                    ? new Vector2f(0, 0.26f)
                                    : new Vector2f(0, -0.26f));

                    GameObject fireball = Prefabs.generateEnemyFireball(position);
                    fireball.getComponent(EnemyFireball.class).goingUp =
                            this.gameObject.transform.scale.y > 0;
                    Window.getScene().addGameObjectToScene(fireball);
                } else if (yOffset > 0) {
                    Vector2f position = new Vector2f(this.gameObject.transform.position)
                            .add(this.gameObject.transform.scale.y > 0
                                    ? new Vector2f(0, -0.26f)
                                    : new Vector2f(0, 0.26f));

                    GameObject fireball = Prefabs.generateEnemyFireball(position);
                    fireball.getComponent(EnemyFireball.class).goingDown =
                            this.gameObject.transform.scale.y > 0;
                    Window.getScene().addGameObjectToScene(fireball);
                }
            }
        }

        this.rb.setVelocity(velocity);
        i++;
    }

    @Override
    public void preSolve(GameObject obj, Contact contact, Vector2f contactNormal) {
        if (isDead) {
            return;
        }

        PlayerController playerController = obj.getComponent(PlayerController.class);
        if (playerController != null) {
            if (!playerController.isDead() && !playerController.isHurtInvincible() &&
                    contactNormal.y > 0.58f) {
                playerController.enemyBounce();
                stomp();
            } else if (!playerController.isDead() && !playerController.isInvincible()) {
                playerController.die();
                if (!playerController.isDead()) {
                    contact.setEnabled(false);
                }
            } else if (!playerController.isDead() && playerController.isInvincible()) {
                contact.setEnabled(false);
            }
        } else if (abs(contactNormal.y) < 0.1f) {
            goingRight = contactNormal.x < 0;
        }

        if (obj.getComponent(Fireball.class) != null) {
            stomp();
            obj.getComponent(Fireball.class).disappear();
        }
    }

    public void stomp() {
        stomp(true);
    }

    public void stomp(boolean playSound) {
        this.isDead = true;
        this.velocity.zero();
        this.rb.setVelocity(new Vector2f());
        this.rb.setAngularVelocity(0.0f);
        this.rb.setGravityScale(0.0f);
        this.stateMachine.trigger("squashMe");
        this.rb.setIsSensor();
        if (playSound) {
            AssetPool.getSound("assets/sounds/bump.ogg").play();
        }
    }
}
