import java.util.List;
import comp34120.ex2.Record;

public class Model{
//linear regression approach
// with forgetting factor

  private float a;
  private float b;
  private float c;
  private int windowSize;

  public Model() {
    a = 1.0f;
    b = 1.0f;
    c = 1.23f;
    windowSize = 40;
  }

  public float getA() {
    return a;
  }

  public float getB() {
    return b;
  }


  public void train(List<Record> records) {
    //leader price^2 * follower reaction - leader price * (leader price * follower reaction)/ data number * leader price^2 - leader price^2
    // data number * leader price * follower reaction -  leader price * follower reaction / data number * leader price^2 - leader price^2
    float follower_s = 0f;
    float c_sum = 0f;
    float squared_leader_s = 0f;
    float leader_s = 0f;
    float leader_times_follower_s = 0f;

    for (int i = records.size() - windowSize; i < records.size(); i++) {

      Record record = records.get(i);
      float forgettingFactor = (float) Math.pow(c, windowSize - i);

      c_sum += forgettingFactor;
      leader_s += forgettingFactor * record.m_leaderPrice;
      squared_leader_s += forgettingFactor * record.m_leaderPrice * record.m_leaderPrice;
      follower_s += forgettingFactor * record.m_followerPrice;
      leader_times_follower_s += forgettingFactor * record.m_leaderPrice * record.m_followerPrice;

    }

    a = (squared_leader_s * follower_s - leader_s * leader_times_follower_s) / (c_sum * squared_leader_s - (leader_s*leader_s));
    b = (c_sum * leader_times_follower_s - leader_s * follower_s) / (c_sum * squared_leader_s - (leader_s * leader_s));

  }
}
