import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author Alexander Kahl
 * @since March 2013
 * @version 1.0.0
 * 
 */
public class JobScheduler {

    /**
     * Date should be formatted as YYYY-MM-DD
     */
    public final static String EARLIEST_DATE = "2013-03-10";
    public final static int INTERVAL_SEC = 10;

    public static void main(String[] args) {

        try {
            // specify the job' s details..
            JobDetail job = JobBuilder.newJob(CheckTweetsJob.class)
                                   .withIdentity("checkTweetsJob")
                                   .build();

            // specify the running period of the job
            Trigger trigger = TriggerBuilder.newTrigger()
                                         .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                                                            .withIntervalInSeconds(INTERVAL_SEC)
                                                                            .repeatForever())
                                          .build();

            // schedule the job
            SchedulerFactory schFactory = new StdSchedulerFactory();
            Scheduler sch = schFactory.getScheduler();
            sch.start();
            sch.scheduleJob(job, trigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
