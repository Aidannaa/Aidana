import java.util.*;
import java.util.concurrent.*;

public class dom12 {
    enum VacancyStatus {PENDING, APPROVED, REVISE}
    enum CandidateStatus {APPLIED, REJECTED, INTERVIEW_INVITED, OFFERED, HIRED}

    static class Vacancy {
        String title;
        VacancyStatus status = VacancyStatus.PENDING;
        Vacancy(String t){ this.title = t; }
    }

    static class Candidate {
        String name;
        CandidateStatus status = CandidateStatus.APPLIED;
        Candidate(String n){ this.name = n; }
        public String toString(){ return name + " (" + status + ")"; }
    }

    public static void main(String[] args) throws Exception {
        Vacancy v = new Vacancy("Java Developer");
        System.out.println("Заявка создана.");

        if(!validate(v)) {
            System.out.println("HR: Требуется доработка.");
            return;
        }
        v.status = VacancyStatus.APPROVED;
        System.out.println("HR подтвердил. Вакансия опубликована.");

        List<Candidate> list = List.of(
                new Candidate("Aibek"),
                new Candidate("Dana"),
                new Candidate("Ernar"),
                new Candidate("Saltanat")
        );

        ExecutorService ex = Executors.newFixedThreadPool(4);
        List<Future<Candidate>> futures = new ArrayList<>();
        for(Candidate c : list) futures.add(ex.submit(() -> checkCandidate(c)));

        for(Future<Candidate> f : futures)
            System.out.println(f.get());
        ex.shutdown();

        for(Candidate c : list) {
            if(c.status == CandidateStatus.INTERVIEW_INVITED) {
                boolean hr = interview("HR", c);
                boolean tech = interview("TechLead", c);
                if(hr && tech) {
                    c.status = CandidateStatus.OFFERED;
                    if(Math.random() > 0.4) {
                        c.status = CandidateStatus.HIRED;
                        System.out.println(c.name + " принят. IT уведомлен.");
                    } else {
                        System.out.println(c.name + " отказался.");
                    }
                } else {
                    c.status = CandidateStatus.REJECTED;
                    System.out.println(c.name + " не прошел интервью.");
                }
            }
        }
    }

    static boolean validate(Vacancy v){
        return v.title.length() > 3;
    }

    static Candidate checkCandidate(Candidate c) throws Exception {
        Thread.sleep((int)(Math.random()*300));
        if(c.name.length() < 4)
            c.status = CandidateStatus.REJECTED;
        else
            c.status = CandidateStatus.INTERVIEW_INVITED;
        return c;
    }

    static boolean interview(String who, Candidate c) throws Exception {
        Thread.sleep((int)(Math.random()*200));
        boolean pass = Math.random() > 0.3;
        System.out.println(who + " interview / " + c.name + ": " + (pass ? "OK" : "FAIL"));
        return pass;
    }
}
