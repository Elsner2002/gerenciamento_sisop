import java.util.Scanner;
import java.util.concurrent.Semaphore;

/**
 * Synchronization object for writing and reading to/from shell.
 */
public class ShellIO {
	private Scanner in;
	private Semaphore semaphore;

	public ShellIO() {
		this.in = new Scanner(System.in);
		this.semaphore = new Semaphore(1, true);
	}

	public int nextInt() {
		return this.getInputInt("");
	}

	public String nextLine() {
		return this.getInputLine("");
	}

	public int getInputInt(String prompt) {
		this.semaphore.acquireUninterruptibly();
		System.out.print(prompt);
		int input = in.nextInt();
		this.semaphore.release();
		return input;
	}

	public String getInputLine(String prompt) {
		this.semaphore.acquireUninterruptibly();
		System.out.print(prompt);
		String input = in.nextLine();
		this.semaphore.release();
		return input;
	}

	public void println(String string) {
		System.out.println(string);
	}
}
