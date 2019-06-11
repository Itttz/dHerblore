package herblore;

import java.util.ArrayList;
import java.util.Random;

public class General
{
	private static String status;
	private static ArrayList<Task> tasks;

	public static String getStatus()
	{
		return status;
	}

	public static void setStatus(String status)
	{
		General.status = status;
	}

	public static ArrayList<Task> getTasks()
	{
		return tasks;
	}

	public static void setTasks(ArrayList<Task> tasks)
	{
		General.tasks = tasks;
	}

	public static Task getCurrentTask()
	{
		if (tasks.size() > 0)
		{
			return tasks.get(0);
		}

		return null;
	}

	public static void getNextTask()
	{
		if (tasks.size() > 0)
		{
			tasks.remove(0);
		}
	}

	public static int random(int min, int max)
	{
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public static double random(double min, double max)
	{
		Random r = new Random();
		return min + (max - min) * r.nextDouble();
	}
}
