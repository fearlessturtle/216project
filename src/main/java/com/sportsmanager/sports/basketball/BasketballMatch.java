package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.AbstractMatch;
import com.sportsmanager.core.MatchEvent;
import com.sportsmanager.core.Player;
import com.sportsmanager.core.Tactic;
import com.sportsmanager.core.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BasketballMatch extends AbstractMatch {

    private List<Player> homeLineup;
    private List<Player> awayLineup;
    private Random random;

    public BasketballMatch(Team homeTeam, Team awayTeam) {
        super(homeTeam, awayTeam);
        this.homeLineup = new ArrayList<>();
        this.awayLineup = new ArrayList<>();
        this.random = new Random();
    }

    @Override
    protected int getPeriodCount() {
        return 4;
    }

    @Override
    protected void simulatePeriod(int periodNumber) {
        int startMinute = (periodNumber - 1) * 12 + 1;
        int endMinute = periodNumber * 12;

        if (periodNumber == 1) {
            homeLineup = new ArrayList<>(homeTeam.selectLineup());
            awayLineup = new ArrayList<>(awayTeam.selectLineup());
        }

        for (int minute = startMinute; minute <= endMinute; minute++) {
            double homeAvg = calcAvg(homeLineup);
            double awayAvg = calcAvg(awayLineup);

            double homeSc = (homeAvg / 100.0) * homeTeam.getTactic().getOffensiveBonus() * 0.35;
            double awaySc = (awayAvg / 100.0) * awayTeam.getTactic().getOffensiveBonus() * 0.35;

            if (random.nextDouble() < homeSc) {
                int pts = random.nextDouble() < 0.25 ? 3 : 2;
                homeScore += pts;
                Player p = pickRandom(homeLineup);
                if (p != null) {
                    notifyObservers(new MatchEvent(MatchEvent.EventType.GOAL, minute,
                            p.getName(), homeTeam.getName(), "Score!"));
                }
            }

            if (random.nextDouble() < awaySc) {
                int pts = random.nextDouble() < 0.25 ? 3 : 2;
                awayScore += pts;
                Player p = pickRandom(awayLineup);
                if (p != null) {
                    notifyObservers(new MatchEvent(MatchEvent.EventType.GOAL, minute,
                            p.getName(), awayTeam.getName(), "Score!"));
                }
            }

            if (random.nextDouble() < 0.04) {
                boolean isHome = random.nextBoolean();
                List<Player> deck = isHome ? homeLineup : awayLineup;
                Player p = pickRandom(deck);
                if (p != null) {
                    notifyObservers(new MatchEvent(MatchEvent.EventType.YELLOW_CARD, minute,
                            p.getName(), isHome ? homeTeam.getName() : awayTeam.getName(), "Foul!"));
                }
            }
        }

        notifyObservers(MatchEvent.periodEnd(endMinute));

        for (Player p : homeLineup) p.applyMatchFatigue();
        for (Player p : awayLineup) p.applyMatchFatigue();
    }

    private double calcAvg(List<Player> lineup) {
        if (lineup.isEmpty()) return 50;
        int sum = 0;
        for (Player p : lineup) sum += p.getOverallRating();
        return (double) sum / lineup.size();
    }

    private Player pickRandom(List<Player> lineup) {
        if (lineup.isEmpty()) return null;
        return lineup.get(random.nextInt(lineup.size()));
    }

    @Override
    public void applyTacticChange(Team team, Tactic newTactic) {
        team.setTactic(newTactic);
    }
}
