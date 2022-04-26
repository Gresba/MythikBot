package Bot;

public class CustomTime {
    private int years;
    private int months;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;

    public CustomTime(long epochSeconds)
    {
        int number = (int) epochSeconds;

        int index = 0;
        while (number != 0) {
            int result = 0;
            switch (index) {
                case 0:
                    result = number / 29030400;
                    number %= 29030400;
                    years = result;
                    break;
                case 1:
                    result = number / 2419200;
                    number %= 2419200;
                    months = result;

                    break;
                case 2:
                    result = number / 86400;
                    number %= 86400;
                    days = result;

                    break;
                case 3:
                    result = number / 3600;
                    number %= 3600;
                    hours = result;

                    break;
                case 4:
                    result = number / 60;
                    number %= 60;
                    minutes = result;

                    break;
                case 5:
                    result = number;
                    number = 0;
                    seconds = result;
                    break;
            }
            index++;
        }
    }

    public int getYears() {
        return years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public int getMonths() {
        return months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        String result = "";
        if(years != 0)
            result += years + " years ";
        if(months != 0)
            result += months + " month ";
        if(days != 0)
            result += days + " days ";
        if(hours != 0)
            result += hours + " hours ";
        if(minutes != 0)
            result += minutes + " minutes ";
        if(seconds != 0)
            result += seconds + " seconds";
        return result;
    }
}
