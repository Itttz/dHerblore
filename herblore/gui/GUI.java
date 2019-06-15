package herblore.gui;

import herblore.General;
import herblore.Task;
import xobot.script.util.Time;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;

public class GUI implements PropertyChangeListener, ListSelectionListener
{
	private int amount = 0;

	private JPanel potionPanel;
	private JPanel selectedPanel;
	private JComboBox potionComboBox;
	private JButton addTaskButton;
	private JPanel mainPanel;
	private JFormattedTextField potionAmountField;
	private JCheckBox makeAllCheckBox;
	private JButton removeTaskButton;
	private JList<String> selectedList;
	private JButton startButton;
	private JPanel startPanel;
	private DefaultListModel<String> listModel;
	private ArrayList<Task> tasks = new ArrayList<>();

	public GUI()
	{
		JFrame frame = new JFrame();
		frame.setContentPane(mainPanel);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("dHerblore");
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);

		listModel = new DefaultListModel<>();
		selectedList.setModel(listModel);
		selectedList.addListSelectionListener(this);

		addTaskButton.addActionListener(e ->
		{
			ArrayList<Integer> ingredients = getIngredients(potionComboBox.getSelectedItem());
			int index = selectedList.getSelectedIndex();

			if (index == -1)
			{
				index = 0;
			} else
			{
				index++;
			}

			int next_selected_index = potionComboBox.getSelectedIndex() + 1;

			if (next_selected_index == potionComboBox.getItemCount())
			{
				next_selected_index--;
			}

			if (makeAllCheckBox.isSelected())
			{
				String name = "Make all " + potionComboBox.getSelectedItem() + " potions";
				listModel.insertElementAt(name, index);
				tasks.add(index, new Task(name, ingredients, -1));
				potionComboBox.setSelectedIndex(next_selected_index);
			} else if (amount > 0)
			{
				String name = "Make " + amount + " " + potionComboBox.getSelectedItem() + " potions";
				listModel.insertElementAt(name, index);
				tasks.add(index, new Task(name, ingredients, amount));
				potionComboBox.setSelectedIndex(next_selected_index);
			}

			if (listModel.getSize() > 0)
			{
				startButton.setEnabled(true);
			}

			if (!makeAllCheckBox.isSelected())
			{
				potionAmountField.requestFocusInWindow();
			}

			selectedList.setSelectedIndex(index);
			selectedList.ensureIndexIsVisible(index);
		});

		removeTaskButton.addActionListener(e ->
		{
			int[] indices = selectedList.getSelectedIndices();
			int index = indices[0];

			for (int i : indices)
			{
				listModel.remove(indices[0]);
				tasks.remove(i);
			}

			int size = listModel.getSize();

			if (size == 0)
			{
				removeTaskButton.setEnabled(false);
				startButton.setEnabled(false);
			} else
			{
				if (index == listModel.getSize())
				{
					index--;
				}

				selectedList.setSelectedIndex(index);
			}
		});

		makeAllCheckBox.addActionListener(e ->
		{
			if (makeAllCheckBox.isSelected())
			{
				potionAmountField.setEnabled(false);
			} else
			{
				potionAmountField.setEnabled(true);
			}
		});

		startButton.addActionListener(e ->
		{
			General.setTasks(tasks);
			frame.dispose();
		});

		while (frame.isVisible() || frame.isActive())
		{
			Time.sleep(500);
		}
	}

	private void createUIComponents()
	{
		potionAmountField = new JFormattedTextField((NumberFormat.getInstance()));
		potionAmountField.setValue(amount);
		potionAmountField.addPropertyChangeListener("value", this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		Object source = e.getSource();
		if (source == potionAmountField)
		{
			amount = ((Number) potionAmountField.getValue()).intValue();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			if (selectedList.getSelectedIndex() == -1)
			{
				removeTaskButton.setEnabled(false);
			} else
			{
				removeTaskButton.setEnabled(true);
			}
		}
	}

	private ArrayList<Integer> getIngredients(Object selection)
	{
		ArrayList<Integer> ids = new ArrayList<>();

		switch ((String) selection)
		{
			case "Attack":
				ids.add(91);
				ids.add(221);
				break;

			case "Anti-Poison":
				ids.add(93);
				ids.add(235);
				break;

			case "Strength":
				ids.add(95);
				ids.add(225);
				break;

			case "Stat Restore":
				ids.add(97);
				ids.add(223);
				break;

			case "Energy":
				ids.add(97);
				ids.add(1975);
				break;

			case "Defence":
				ids.add(99);
				ids.add(239);
				break;

			case "Combat":
				ids.add(97);
				ids.add(9736);
				break;

			case "Prayer":
				ids.add(99);
				ids.add(231);
				break;

			case "Super Attack":
				ids.add(101);
				ids.add(221);
				break;

			case "Super Anti-poison":
				ids.add(101);
				ids.add(235);
				break;

			case "Super Energy":
				ids.add(103);
				ids.add(2970);
				break;

			case "Super Strength":
				ids.add(105);
				ids.add(225);
				break;

			case "Super Restore":
				ids.add(3004);
				ids.add(223);
				break;

			case "Super Defence":
				ids.add(107);
				ids.add(239);
				break;

			case "Antifire":
				ids.add(2483);
				ids.add(241);
				break;

			case "Ranging":
				ids.add(109);
				ids.add(245);
				break;

			case "Magic":
				ids.add(2483);
				ids.add(3138);
				break;

			case "Stamina":
				ids.add(3018);
				ids.add(42640);
				break;

			case "Antidote++":
				ids.add(35951);
				ids.add(6051);
				break;

			case "Saradomin Brew":
				ids.add(3002);
				ids.add(6693);
				break;

			case "Recover Special":
				ids.add(3018);
				ids.add(5972);
				break;

			case "Super Antifire":
				ids.add(2454);
				ids.add(4621);
				break;

			case "Super Combat":
				ids.add(145);
				ids.add(157);
				ids.add(163);
				ids.add(269);
				break;

			case "Prayer Renewal":
				ids.add(21628);
				ids.add(21622);
				break;

			case "Extreme Attack":
				ids.add(145);
				ids.add(261);
				break;

			case "Extreme Strength":
				ids.add(157);
				ids.add(267);
				break;

			case "Extreme Defence":
				ids.add(163);
				ids.add(2481);
				break;

			case "Extreme Ranging":
				ids.add(169);
				ids.add(12539);
				break;

			case "Extreme Magic":
				ids.add(3042);
				ids.add(9594);
				break;

			case "Overload":
				ids.add(15309);
				ids.add(15313);
				ids.add(15317);
				ids.add(15325);
				ids.add(15321);
				ids.add(269);
				break;

			default:
				System.out.println(selection);
				break;
		}

		return ids;
	}
}
