package jobLog;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

public class JobLogMain {

    public static void main(String[] args) throws SchedulerException {
        System.out.println("--------- Job Logs Health Check Scheduler Started ----------");

        JobDetail job = newJob(jobLog.JobLogImpl.class)
                .withIdentity("JobLogHealthCheck", "group1")
                .build();

        // Run every 1 hour
        Trigger trigger = newTrigger()
                .withIdentity("triggerJobLog", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInHours(1) // every 1 hour
                        .repeatForever())
                .forJob(job)
                .build();

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);

        System.out.println("Scheduler is running...");
    }
}
