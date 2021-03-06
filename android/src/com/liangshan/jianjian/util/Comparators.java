/**
 * Copyright 
 */

package com.liangshan.jianjian.util;

import com.liangshan.jianjian.android.util.StringFormatters;
import com.liangshan.jianjian.types.Event;
import com.liangshan.jianjian.types.RecommendMsg;
import com.liangshan.jianjian.types.User;
import com.liangshan.jianjian.types.Venue;

import java.text.ParseException;
import java.util.Comparator;

/**
 */
public class Comparators {

    private static Comparator<Venue> sVenueDistanceComparator = null;
    private static Comparator<User> sUserRecencyComparator = null;
    private static Comparator<Event> sEventRecencyComparator = null;
    private static Comparator<Event> sEventDistanceComparator = null;
    private static Comparator<RecommendMsg> sRecommendsRecencyComparator = null;

    public static Comparator<Venue> getVenueDistanceComparator() {
        if (sVenueDistanceComparator == null) {
            sVenueDistanceComparator = new Comparator<Venue>() {
                /*
                 * (non-Javadoc)
                 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                 */
                @Override
                public int compare(Venue object1, Venue object2) {
                    // TODO: In practice we're pretty much guaranteed to get valid locations
                    // from foursquare, but.. what if we don't, what's a good fail behavior
                    // here?
                    try {
                        int d1 = Integer.parseInt(object1.getDistance());
                        int d2 = Integer.parseInt(object2.getDistance());
                        
                        if (d1 < d2) {
                            return -1;
                        } else if (d1 > d2) {
                            return 1;
                        } else {
                            return 0;
                        }
                    } catch (NumberFormatException ex) {
                        return object1.getDistance().compareTo(object2.getDistance());
                    }
                }
            };
        }
        return sVenueDistanceComparator;
    }

    public static Comparator<Venue> getVenueNameComparator() {
        if (sVenueDistanceComparator == null) {
            sVenueDistanceComparator = new Comparator<Venue>() {
                /*
                 * (non-Javadoc)
                 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                 */
                @Override
                public int compare(Venue object1, Venue object2) {
                    return object1.getName().toLowerCase().compareTo(
                            object2.getName().toLowerCase());
                }
            };
        }
        return sVenueDistanceComparator;
    }

    public static Comparator<Event> getEventRecencyComparator() {
        if (sEventRecencyComparator == null) {
            sEventRecencyComparator = new Comparator<Event>() {
                /*
                 * (non-Javadoc)
                 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                 */
                @Override
                public int compare(Event object1, Event object2) {
                    try {
                        return StringFormatters.DATE_FORMAT.parse(object2.getCreateDate()).compareTo(
                                StringFormatters.DATE_FORMAT.parse(object1.getCreateDate()));
                    } catch (ParseException e) {
                        return 0;
                    }
                }
            };
        }
        return sEventRecencyComparator;
    }
    
    public static Comparator<Event> getEventDistanceComparator() {
        if (sEventDistanceComparator == null) {
            sEventDistanceComparator = new Comparator<Event>() {
                /*
                 * (non-Javadoc)
                 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                 */
                @Override
                public int compare(Event object1, Event object2) {
                    try {
                        /*
                        int d1 = Integer.parseInt(object1.getDistance());
                        int d2 = Integer.parseInt(object2.getDistance());
                        if (d1 > d2) {
                            return 1;
                        } else if (d2 > d1) {
                            return -1;
                        } else {
                            return 0;
                        }*/
                        return 1;
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                }
            };
        }
        return sEventDistanceComparator;
    }


    public static Comparator<RecommendMsg> getRecommendsRecencyComparator() {
        if (sRecommendsRecencyComparator == null) {
            sRecommendsRecencyComparator = new Comparator<RecommendMsg>() {
                /*
                 * (non-Javadoc)
                 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
                 */
                @Override
                public int compare(RecommendMsg object1, RecommendMsg object2) {
                    try {
                        return StringFormatters.DATE_FORMAT.parse(object2.getCreateDate()).compareTo(
                                StringFormatters.DATE_FORMAT.parse(object1.getCreateDate()));
                    } catch (ParseException e) {
                        return 0;
                    }
                }
            };
        }
        return sRecommendsRecencyComparator;
    }


}
