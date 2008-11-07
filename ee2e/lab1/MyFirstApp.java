import java.util.*;

public class MyFirstApp
{
	public static void main(String args[])
	{
		String firstName = args[0];
		String lastName = args[1];
		String fullName;
		fullName = firstName + " " + lastName;
     	System.out.println("Forwards: \n" + fullName + "\n"); 	//Name forwards

     	int Length = fullName.length();

     	System.out.println("Backwards: ");

     	for(int n=Length-1; n>=0; n--)							//Name Backwards
     	{
			System.out.print(fullName.charAt(n));
		}
		System.out.print("\n");

		char[] nameArray = new char[Length];
		nameArray = fullName.toCharArray();
		Arrays.sort(nameArray);
		System.out.println("\nSorted: ");
	    System.out.println(nameArray);							//Name Sorted


	}
}
