package herblore;

import xobot.script.methods.Bank;
import xobot.script.methods.Game;
import xobot.script.methods.Packets;
import xobot.script.methods.input.KeyBoard;
import xobot.script.methods.tabs.Inventory;
import xobot.script.util.Time;
import xobot.script.wrappers.interactive.Item;

public class Banking
{
	public static void withdrawX(int id, int amount)
	{
		try
		{
			if (Bank.isOpen())
			{
				Item item = Bank.getItem(id);

				if (item != null)
				{
					General.setStatus("Withdrawing ingredient");
					if (Game.getInputState() != 1)
					{
						Packets.sendAction("Withdraw-X", String.format("<col=ff9040>%s</col>", item.getDefinition().getName()), 53, item.getID(), item.getSlot(), 18687, item.getSlot());
						Time.sleep(() -> Game.getInputState() == 1, General.random(1000, 1500));
					}

					if (Game.getInputState() == 1)
					{
						Game.setInputText(String.valueOf(amount));
						Time.sleep(() -> Game.getInputText().equals(String.valueOf(amount)), General.random(1000, 1500));

						if (Game.getInputText().equals(String.valueOf(amount)))
						{
							Item inventory_item = Inventory.getItem(id);
							int count = Inventory.getCount(id);
							int stack = inventory_item != null ? inventory_item.getStack() : 0;

							KeyBoard.pressEnter();
							Time.sleep(() ->
							{
								Item item1 = Inventory.getItem(id);
								return Inventory.getCount(id) > count || (item1 != null && item1.getStack() > stack);
							}, General.random(1500, 3500));
						}
					}
				}
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
	}

	public static int getWithdrawAmount(int id)
	{
		Task task = General.getCurrentTask();

		if (task == null)
		{
			return 0;
		}

		int size = task.getIds().size();
		int stackable = 0;

		for (Integer i : task.getIds())
		{
			if (i == 12539 || i == 42640)
			{
				size--;
				stackable++;
			}
		}

		int withdraw_amount = ((28 / size) - stackable);

		if (id != 12539 && id != 42640)
		{
			withdraw_amount -= Inventory.getCount(id);
		} else
		{
			Item item = Inventory.getItem(id);
			int stack = id == 12539 ? 5 : 1;
			if (item != null)
			{
				withdraw_amount = (withdraw_amount * stack) - item.getStack();
			} else
			{
				withdraw_amount *= stack;
			}
		}

		if (task.getAmount() != -1)
		{
			int stack = id == 12539 ? 5 : 1;
			if ((id == 12539 || id == 42640) && withdraw_amount > (task.getAmount() * stack))
			{
				Item item = Inventory.getItem(id);
				if (item != null)
				{
					withdraw_amount = (task.getAmount() * stack) - item.getStack();
				} else
				{
					withdraw_amount = (task.getAmount() * stack);
				}
			} else if (withdraw_amount > task.getAmount())
			{
				withdraw_amount = task.getAmount() - Inventory.getCount(id);
			}
		}

		return withdraw_amount;
	}

	public static Boolean hasIngredients()
	{
		Task task = General.getCurrentTask();

		if (task == null)
		{
			return null;
		}

		for (Integer i : task.getIds())
		{
			try
			{
				Item bank_item = Bank.getItem(i);
				Item inv_item = Inventory.getItem(i);

				if ((inv_item == null && bank_item == null))
				{
					return false;
				} else if (i == 12539)
				{
					if ((inv_item != null && inv_item.getStack() < 5) && (bank_item != null && bank_item.getStack() < 5))
					{
						return false;
					} else if (inv_item != null && inv_item.getStack() < 5)
					{
						return false;
					} else if (bank_item != null && bank_item.getStack() < 5)
					{
						return false;
					}
				}
			} catch (NullPointerException | ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e)
			{
				e.printStackTrace();
				return null;
			}
		}

		return true;
	}

	public static boolean shouldDepositAll()
	{
		Task task = General.getCurrentTask();

		if (task != null)
		{
			for (Item item : Inventory.getUniqueItems())
			{
				if (!task.getIds().contains(item.getID()) || getWithdrawAmount(item.getID()) < 0)
				{
					return true;
				}
			}
		}

		return false;
	}
}
