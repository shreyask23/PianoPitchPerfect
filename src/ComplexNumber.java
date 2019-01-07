import java.math.*;

public class ComplexNumber {

	private double realPart;
	private double imaginaryPart;
	
	public ComplexNumber(double real, double imaginary) {
		realPart = real;
		imaginaryPart = imaginary;
	}
	
	public ComplexNumber(double theta) {
		//Constructor for Euler's Formula: e ^ (j * theta)= cos(theta) + j * sin(theta)
		realPart = Math.cos(theta);
		imaginaryPart = Math.sin(theta);
	}
	
	public ComplexNumber add(ComplexNumber addend) {
		return new ComplexNumber(realPart + addend.getReal(), imaginaryPart + addend.getImaginary());
	}
	
	public ComplexNumber mult(ComplexNumber factor) {
		double r1 = realPart;
		double j1 = imaginaryPart;
		double r2 = factor.getReal();
		double j2 = factor.getImaginary();
		return new ComplexNumber(r1 * r2 - j1 * j2, r1 * j2 + j1 * r2);
	}
	
	public ComplexNumber exp(int exponent) {
		ComplexNumber power = new ComplexNumber(1, 0);
		for (int k = 0; k < exponent; k++) {
			power = mult(power);
		}
		return power;
	}
	
	public double magnitude() {
		return Math.sqrt(realPart * realPart + imaginaryPart * imaginaryPart);
	}
	
	public String toString() {
		return Double.toString(realPart) + " + " + Double.toString(imaginaryPart) + "j";
	}
	
	public double getReal() {
		return realPart;
	}
	
	public double getImaginary() {
		return imaginaryPart;
	}
	
}
