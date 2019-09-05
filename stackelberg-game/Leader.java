import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

final class Leader
			extends PlayerImpl
{

	private List<Record> data;

	private Model model;

	private static float a1 = 2.0f;
	private static float b1 = -1;
	private static float c1 = 1.0f;
	private static float d1 = 0.3f;

	private Leader() throws RemoteException, NotBoundException
	{
		super(PlayerType.LEADER, "Leader");
	}

	@Override
	public void goodbye() throws RemoteException
	{
		ExitTask.exit(500);
	}

	@Override
	public void startSimulation(int p_steps) throws RemoteException
	{
		data = getData(100);
		// for(Record record : data) {
		// 	System.out.println(String.format("Record %d :: Leader: %f, Follower: %f", record.m_date, record.m_leaderPrice, record.m_followerPrice));
		// }
		model = new Model();
	}

	/**
	 * To inform this instance to proceed to a new simulation day
	 * @param p_date The date of the new day
	 * @throws RemoteException
	 */
	@Override
	public void proceedNewDay(int p_date) throws RemoteException
	{
		data = getData(p_date);
		Record record = data.get(data.size() - 1);

		//train the model
		model.train(data);

		// Calculate optimal price
		float optimal_price = maximize(model);

		// publish the price
		m_platformStub.publishPrice(m_type, optimal_price);

	}



	private List<Record> getData(int endDate) throws RemoteException{
		List<Record> records = new ArrayList<Record>();
		for (int date = 1; date < endDate; date++) {
			records.add(m_platformStub.query(PlayerType.LEADER, date));
		}
		return records;
	}
	//maximizer
	public static float maximize(Model model) {
		float a = model.getA();
		float b = model.getB();
		//calculated the numerator/denominator to get max
		float max = (c1+d1)*(c1+d1*d1)*(c1*d1*b-a1-d1*a)/(2*(b1+d1*b));
		return max;
	}

	public static void main(final String[] p_args)
		throws RemoteException, NotBoundException
	{
		new Leader();
	}

	/**
	 * The task used to automatically exit the leader process
	 * @author Xin
	 */
	private static class ExitTask
		extends TimerTask
	{
		static void exit(final long p_delay)
		{
			(new Timer()).schedule(new ExitTask(), p_delay);
		}

		@Override
		public void run()
		{
			System.exit(0);
		}
	}
}
