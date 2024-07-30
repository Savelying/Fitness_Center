package savelying;

public interface Calculator <T extends Number> {
	int calculateFees(T clubId);
}
