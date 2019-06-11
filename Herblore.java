import herblore.Banking;
import herblore.General;
import herblore.Task;
import herblore.gui.GUI;
import xobot.client.callback.listeners.MessageListener;
import xobot.client.callback.listeners.PaintListener;
import xobot.client.events.MessageEvent;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.Bank;
import xobot.script.methods.Game;
import xobot.script.methods.GameObjects;
import xobot.script.methods.Packets;
import xobot.script.methods.input.KeyBoard;
import xobot.script.methods.tabs.Inventory;
import xobot.script.methods.tabs.Skills;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.interactive.GameObject;
import xobot.script.wrappers.interactive.Item;
import xobot.script.wrappers.interactive.Player;

import java.awt.*;
import java.awt.event.KeyEvent;

@Manifest(name = "dHerblore",
		  authors = "Dirupt",
		  server = "Ikov",
		  description = "Make sure ingredients are in main tab")
public class Herblore extends ActiveScript implements PaintListener, MessageListener
{
	private Timer timer;
	private Timer mixing;

	private final int start_xp = Skills.HERBLORE.getCurrentExp();

	@Override
	public boolean onStart()
	{
		new GUI();
		timer = new Timer();
		return General.getTasks() != null;
	}

	@Override
	public int loop()
	{
		if (Game.isLoggedIn() && General.getCurrentTask() != null)
		{
			if (hasAllIngredientsInInventory())
			{
				mix();
			} else
			{
				bank();
			}

			return 500;
		}

		return -1;
	}

	private boolean hasAllIngredientsInInventory()
	{
		Task task = General.getCurrentTask();

		if (task == null)
		{
			return false;
		}

		for (Integer i : task.getIds())
		{
			Item item = Inventory.getItem(i);

			if (item == null || (item.getID() == 12539 && item.getStack() < 5))
			{
				return false;
			}
		}

		return true;
	}

	private void mix()
	{
		Task task = General.getCurrentTask();

		if (Player.getMyPlayer().getAnimation() == 363)
		{
			mixing = new Timer();
		}

		if (Game.getInputState() != 6 && task != null && (mixing == null || mixing.getElapsed() > 2000))
		{
			General.setStatus("Mixing ingredients");
			Item[] ingredients = getIngredients();

			if (ingredients != null)
			{
				Packets.sendAction("Use", String.format("<col=ff9040>%s</col>", ingredients[0].getDefinition().getName()), 447, ingredients[0].getID(), ingredients[0].getSlot(), 3214, 0);
				Packets.sendAction(String.format("Use %s with", ingredients[0].getDefinition().getName()), String.format("<col=ff9040%s</col>", ingredients[1].getDefinition().getName()), 870, ingredients[1].getID(), ingredients[1].getSlot(), 3214, 0);
				Time.sleep(() -> Game.getInputState() == 6, General.random(1500, 2000));
			}
		}

		if (Game.getInputState() == 6)
		{
			KeyBoard.pressKey(KeyEvent.VK_1);
			Time.sleep(() -> Player.getMyPlayer().getAnimation() == 363, General.random(1500, 2000));
		}
	}

	private void bank()
	{
		if (!Bank.isOpen())
		{
			GameObject booth = GameObjects.getNearest(2213);

			if (booth != null)
			{
				General.setStatus("Opening bank");
				booth.interact("Use");
				Time.sleep(Bank::isOpen, General.random(1500, 3500));
			}
		}

		if (Bank.isOpen())
		{
			Task task = General.getCurrentTask();
			Boolean hasIngredients = Banking.hasIngredients();

			if (hasIngredients != null && hasIngredients && task != null && (task.getAmount() > 0 || task.getAmount() == -1))
			{
				if (Banking.shouldDepositAll())
				{
					General.setStatus("Depositing items");
					Bank.depositAll();
					Time.sleep(Inventory::isEmpty, General.random(1500, 2000));
				}

				for (Integer i : task.getIds())
				{
					int withdraw_amount = Banking.getWithdrawAmount(i);
					if (withdraw_amount > 0)
					{
						Banking.withdrawX(i, withdraw_amount);
					}
				}
			} else
			{
				if (hasIngredients != null || (task != null && task.getAmount() == 0))
				{
					General.setStatus("Moving on to next task");
					General.getNextTask();
				}
			}
		}
	}

	private Item[] getIngredients()
	{
		Task task = General.getCurrentTask();

		if (task != null)
		{
			Item ingredient1 = Inventory.getItem(task.getIds().get(0));
			Item ingredient2 = Inventory.getItem(task.getIds().get(1));

			if (task.getName().contains("Overload"))
			{
				ingredient1 = Inventory.getItem(15309);
				ingredient2 = Inventory.getItem(269);
			}

			if (ingredient1 != null && ingredient2 != null)
			{
				return new Item[]{ingredient1, ingredient2};
			}
		}

		return null;
	}

	@Override
	public void repaint(Graphics g)
	{
		Task task = General.getCurrentTask();

		int xp = Skills.HERBLORE.getCurrentExp() - start_xp;
		int ph = (int) ((xp) * 3600000D / (timer.getElapsed()));

		g.drawString("Runtime: " + timer.toElapsedString(), 10, 100);
		g.drawString("Status: " + General.getStatus(), 10, 120);
		g.drawString(String.format("XP: %d (%d/hr)", xp, ph), 10, 140);

		if (task != null)
		{
			String amount = task.getAmount() != -1 ? " (" + task.getAmount() + " remaining)" : "";
			g.drawString("Current task: " + task.getName() + amount, 10, 180);
		}
	}

	@Override
	public void MessageRecieved(MessageEvent me)
	{
		if (me.getType() == 0)
		{
			if (me.getMessage().contains("You make a "))
			{
				Task task = General.getCurrentTask();

				if (task != null && task.getAmount() > 0)
				{
					task.decrementAmount();
				}
			}
		}
	}
}
