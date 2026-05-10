package com.sportsmanager.sports.basketball;

import com.sportsmanager.core.AbstractMatch;
import com.sportsmanager.core.AbstractPlayer;
import com.sportsmanager.core.MatchEvent;
import com.sportsmanager.core.Player;
import com.sportsmanager.core.Tactic;
import com.sportsmanager.core.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class BasketballMatch extends AbstractMatch {

    private List<Player> homeLineup;
    private List<Player> awayLineup;
    private Random random;
    private Set<Player> homeMatchParticipants;
    private Set<Player> awayMatchParticipants;
    private Set<Player> homePeriodParticipants;
    private Set<Player> awayPeriodParticipants;

    public BasketballMatch(Team homeTeam, Team awayTeam) {
        super(homeTeam, awayTeam);
        this.homeLineup = new ArrayList<>();
        this.awayLineup = new ArrayList<>();
        this.random = new Random();
        this.homeMatchParticipants = new LinkedHashSet<>();
        this.awayMatchParticipants = new LinkedHashSet<>();
        this.homePeriodParticipants = new LinkedHashSet<>();
        this.awayPeriodParticipants = new LinkedHashSet<>();
    }

    @Override
    protected int getPeriodCount() {
        return 4;
    }

    @Override
    protected int getPeriodLength() {
        return 12;
    }

    @Override
    protected void simulatePeriod(int periodNumber) {
        int startMinute = (periodNumber - 1) * 12 + 1;
        int endMinute = periodNumber * 12;

        if (periodNumber == 1) {
            homeLineup = new ArrayList<>(homeTeam.selectLineup());
            awayLineup = new ArrayList<>(awayTeam.selectLineup());
            homeMatchParticipants.clear();
            awayMatchParticipants.clear();
        }

        beginPeriodTracking();
        for (int minute = startMinute; minute <= endMinute; minute++) {
            double homeAvg = calcAvg(homeLineup);
            double awayAvg = calcAvg(awayLineup);

            double homeOff = homeTeam.getTactic() != null ? homeTeam.getTactic().getOffensiveBonus() : 1.0;
            double awayOff = awayTeam.getTactic() != null ? awayTeam.getTactic().getOffensiveBonus() : 1.0;
            double homeSc = (homeAvg / 100.0) * homeOff * 0.35;
            double awaySc = (awayAvg / 100.0) * awayOff * 0.35;

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

        for (Player p : homePeriodParticipants) p.applyMatchFatigue();
        for (Player p : awayPeriodParticipants) p.applyMatchFatigue();

        if (periodNumber == getPeriodCount()) {
            recoverInactivePlayers(homeTeam, homeMatchParticipants);
            recoverInactivePlayers(awayTeam, awayMatchParticipants);
            maybeInjurePlayer(homeLineup, homeTeam, endMinute, 0.06, 1, 2);
            maybeInjurePlayer(awayLineup, awayTeam, endMinute, 0.06, 1, 2);
        }
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
        if (team != null && newTactic != null) {
            team.setTactic(newTactic);
        }
    }

    @Override
    public void substitutePlayer(Team team, Player playerOut, Player playerIn) {
        if (team == null || playerOut == null || playerIn == null) {
            return;
        }

        if (!team.equals(homeTeam) && !team.equals(awayTeam)) {
            return;
        }

        List<Player> roster = team.getPlayers();
        if (!roster.contains(playerOut) || !roster.contains(playerIn)) {
            return;
        }

        List<Player> lineup = team.equals(homeTeam) ? homeLineup : awayLineup;
        if (lineup != null && lineup.remove(playerOut) && !lineup.contains(playerIn)) {
            lineup.add(playerIn);
            registerParticipation(team, playerOut);
            registerParticipation(team, playerIn);
        }
    }

    private void recoverInactivePlayers(Team team, Set<Player> participants) {
        Set<Player> playedPlayers = participants != null ? participants : Set.of();
        for (Player player : team.getPlayers()) {
            if (player != null && !playedPlayers.contains(player)) {
                player.recoverOneGame();
            }
        }
    }

    private void beginPeriodTracking() {
        homePeriodParticipants = new LinkedHashSet<>();
        awayPeriodParticipants = new LinkedHashSet<>();
        registerLineupParticipation(homeTeam, homeLineup);
        registerLineupParticipation(awayTeam, awayLineup);
    }

    private void registerLineupParticipation(Team team, List<Player> lineup) {
        if (lineup == null) {
            return;
        }

        for (Player player : lineup) {
            registerParticipation(team, player);
        }
    }

    private void registerParticipation(Team team, Player player) {
        if (team == null || player == null) {
            return;
        }

        if (team.equals(homeTeam)) {
            if (homeMatchParticipants == null) {
                homeMatchParticipants = new LinkedHashSet<>();
            }
            if (homePeriodParticipants == null) {
                homePeriodParticipants = new LinkedHashSet<>();
            }
            homeMatchParticipants.add(player);
            homePeriodParticipants.add(player);
        } else if (team.equals(awayTeam)) {
            if (awayMatchParticipants == null) {
                awayMatchParticipants = new LinkedHashSet<>();
            }
            if (awayPeriodParticipants == null) {
                awayPeriodParticipants = new LinkedHashSet<>();
            }
            awayMatchParticipants.add(player);
            awayPeriodParticipants.add(player);
        }
    }

    private void maybeInjurePlayer(List<Player> lineup,
                                   Team team,
                                   int minute,
                                   double chance,
                                   int minGames,
                                   int maxGames) {
        if (lineup == null || lineup.isEmpty() || random.nextDouble() >= chance) {
            return;
        }

        Player victim = pickRandom(lineup);
        if (!(victim instanceof AbstractPlayer abstractPlayer)) {
            return;
        }

        int games = minGames + random.nextInt(Math.max(1, maxGames - minGames + 1));
        abstractPlayer.injure(games);
        notifyObservers(new MatchEvent(
                MatchEvent.EventType.INJURY,
                minute,
                victim.getName(),
                team.getName(),
                victim.getName() + " will miss " + games + " game(s)"
        ));
    }
}
