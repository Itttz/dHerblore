package herblore;

import java.util.ArrayList;

public class Task
{
	private String name;
	private ArrayList<Integer> ids;
	private Integer amount;

	public Task(String name, ArrayList<Integer> ids, Integer amount)
	{
		this.name = name;
		this.ids = ids;
		this.amount = amount;
	}

	public String getName()
	{
		return name;
	}

	public ArrayList<Integer> getIds()
	{
		return ids;
	}

	public Integer getAmount()
	{
		return amount;
	}

	public void decrementAmount()
	{
		amount--;
	}
}
