package me.esshd.hcf.eventgame;

import java.util.*;
import java.time.*;

public interface IEventScheduler
{
    Map<LocalDateTime, String> getScheduleMap();
}
