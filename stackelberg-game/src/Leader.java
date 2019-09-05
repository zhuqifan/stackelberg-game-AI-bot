import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.Record;
import src.Model;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

final class Leader
			extends PlayerImpl
{

	private List<Record> data;

	// R(u_l) = a + b(u_l)
	private Model reaction;

	private Solver solver;

	private Leader()	throws RemoteException, NotBoundException
	{
		super(PlayerType.LEADER, "Leader");
		solver = new LinearRegressionSolver();
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
		reaction = new ForgettingModel();
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
		System.out.println(String.format("Leader: %f, Follower: %f, Predicted: %f\n", record.m_leaderPrice, record.m_followerPrice, reaction.predict(record.m_leaderPrice)));
		// float profit = (recent.m_leaderPrice - recent.m_cost) * (2 - recent.m_leaderPrice + 0.3f * recent.m_followerPrice);
		// System.out.println(String.format("Profit: %f", profit));
		reaction.train(data);
		// for(Record record : data) {
		// 	System.out.println(String.format("Leader: %f, Follower: %f, Predicted: %f\n", record.m_leaderPrice, record.m_followerPrice, reaction.predict(record.m_leaderPrice)));
		// }
		// Calculate our price
		float u_l = solver.maximize(reaction);

		// Send calculated price
		m_platformStub.publishPrice(m_type, u_l);

		// TODO:Update our model
	}

	/**
	 * Generate a random price based Gaussian distribution. The mean is p_mean,
	 * and the diversity is p_diversity
	 * @param p_mean The mean of the Gaussian distribution
	 * @param p_diversity The diversity of the Gaussian distribution
	 * @return The generated price
	 */
	private float genPrice(final float p_mean, final float p_diversity)
	{
		return 1.6f;
	}

	private List<Record> getData(int endDate) throws RemoteException{
		List<Record> records = new ArrayList<Record>();
		for (int date = 1; date < endDate; date++) {
			records.add(m_platformStub.query(PlayerType.LEADER, date));
		}
		return records;
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
