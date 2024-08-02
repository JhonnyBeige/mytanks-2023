package gtanks.battles.tanks.hulls;

public class Hull {
   public float mass;
   public float power;
   public float speed;
   public float turnSpeed;
   public float hp;
   public float maxSpeed;
   public float acceleration;
   public float reverseAcceleration;
   public float deceleration;
   public float turnMaxSpeed;
   public float turnAcceleration;
   public float turnReverseAcceleration;
   public float turnDeceleration;
   public float sideAcceleration;
   public float damping;

   public Hull(float mass, float power, float speed, float turnSpeed, float hp, float maxSpeed, float acceleration, float reverseAcceleration, float deceleration, float turnMaxSpeed, float turnAcceleration, float turnReverseAcceleration, float turnDeceleration, float sideAcceleration, float damping) {
      this.mass = mass;
      this.power = power;
      this.speed = speed;
      this.turnSpeed = turnSpeed;
      this.hp = hp;
      this.maxSpeed = maxSpeed;
      this.acceleration = acceleration;
      this.reverseAcceleration = reverseAcceleration;
      this.deceleration = deceleration;
      this.turnMaxSpeed = turnMaxSpeed;
      this.turnAcceleration = turnAcceleration;
      this.turnReverseAcceleration = turnReverseAcceleration;
      this.turnDeceleration = turnDeceleration;
      this.sideAcceleration = sideAcceleration;
      this.damping = damping;
   }
}
