package de.lucky44.raycasting.engine.math;

/**
 * Implementation of a 2D Vector
 * @author Nick Balischewski
 */
public class vec2D {
    /**
     * The x and y coordinates of the Vector
     */
    public double x,y;

    /**
     * A vector with x=0 and y=0
     */
    public static final vec2D ZERO = new vec2D(0,0);

    /**
     * creates a new Vector with the provided x and y values
     * @param x the x value of the new Vector
     * @param y the y value of the new Vector
     */
    public vec2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * Adds the provided Vector to this Vector instance
     * @param b the second Vector
     * @return this Vector after the Addition
     */
    public vec2D add(vec2D b){
        this.x += b.x;
        this.y += b.y;
        return this;
    }

    /**
     * Subtracts the provided Vector from this Vector
     * @param b the second Vector
     * @return this Vector after the subtraction
     */
    public vec2D subtract(vec2D b){
        this.x -= b.x;
        this.y -= b.y;
        return this;
    }

    /**
     * multiplies this Vector with the provided Vector
     * @param b the second Vector
     * @return this Vector after the multiplication
     */
    public vec2D multiply(vec2D b){
        this.x *= b.x;
        this.y *= b.y;
        return this;
    }

    /**
     * Multiplies this Vector with the given value
     * @param b the value
     * @return this Vector after the multiplication
     */
    public vec2D multiply(double b){
        this.x *= b;
        this.y *= b;
        return this;
    }

    /**
     * Divides this Vector by the provided Vector
     * @param b the second Vector
     * @return this Vector after the divide
     */
    public vec2D divide(vec2D b){
        this.x /= b.x;
        this.y /= b.y;
        return this;
    }

    /**
     * Divides this Vector by the given value
     * @param b the value
     * @return this Vector after the divide
     */
    public vec2D divide(double b){
        this.x /= b;
        this.y /= b;
        return this;
    }

    /**
     * @return The Magnitude of this Vector
     */
    public double magnitude(){
        return Math.sqrt(x*x + y*y);
    }

    /**
     * Normalizes this Vector
     * @return this Vector after the normalization
     */
    public vec2D normalize(){
        divide(magnitude());
        return this;
    }

    /**
     * Calculates the angle between this Vector and the given other Vector
     * @param b the other Vector
     * @return the angle between Vector a and b
     */
    public double angleBetween(vec2D b){
        return Math.acos(x * b.x + y * b.y);
    }

    /**
     * Calculates the Distance between this Vector and a given other Vector
     * @param b the other Vector
     * @return The distance between Vector a and b
     */
    public double distance(vec2D b){
        return Math.sqrt((b.x - x) * (b.x - x) + (b.y - y) * (b.y - y));
    }

    /**
     * Same as int distance(vec2D b) but the Vector is provided in its elemental form
     * @param x1 the X Coordinate of the second Vector
     * @param y1 the Y Coordinate of the second Vector
     * @return the Distance between this Vector and the provided Vector
     */
    public double distance(double x1, double y1){
        return Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y));
    }

    /**
     * Creates a new Instance of a Vector with the same values as this one
     * @return the new Instance
     */
    public vec2D copy() {
        return new vec2D(x,y);
    }

    /**
     * @return The Vector in text form (x,y)
     */
    public String toString(){
        return "(" + x + "," + y + ")";
    }

    /**
     * Sets the values of this Vector to the values of the provided Vector
     * @param b the other vector
     */
    public void set(vec2D b) {
        this.x = b.x;
        this.y = b.y;
    }
}
