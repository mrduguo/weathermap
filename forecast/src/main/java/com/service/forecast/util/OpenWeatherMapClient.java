package com.service.forecast.util;

import com.service.forecast.controller.ForecastImplDelegate;
import com.service.forecast.entity.objective.DateListItem;
import com.service.forecast.entity.objective.ForecastSummary;
import com.service.forecast.entity.original.ForecastData;
import com.service.forecast.entity.original.ListItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenWeatherMapClient
 */
public final class OpenWeatherMapClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ForecastImplDelegate.class);

    // Metric: Celsius, Imperial: Fahrenheit
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/forecast?appid=763d8bb819e1b0fb58c8385ddd26856e&units=metric&q=";

    public static ForecastSummary showForecastWeather(String city)
    {
        ForecastSummary summary = new ForecastSummary();
        try
        {
            ForecastData forecastData = RestTemplateProxy.INSTANCE.getRestTemplate()
                    .getForObject(StringUtils.join(API_URL, city), ForecastData.class);

            summary.setCityName(forecastData.getCity().getName());
            summary.setCountry(forecastData.getCity().getCountry());
            summary.setCoordinatesLat(forecastData.getCity().getCoord().getLat());
            summary.setCoordinatesLon(forecastData.getCity().getCoord().getLon());

            List<DateListItem> dateListItemList = new ArrayList<DateListItem>();
            for (ListItem i : forecastData.getList())
            {
                dateListItemList.add(toDateListItem(i));
            }
            summary.setDateList(dateListItemList);
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to get the forecast weather data form OpenWeatherMap.", e);
        }

        return summary;
    }

    private static DateListItem toDateListItem(ListItem item)
    {
        DateListItem dateListItem = new DateListItem();
        dateListItem.setDate(item.getDt());
        dateListItem.setDateTxt(item.getDtTxt());
        dateListItem.setWeather(item.getWeather().get(0).getDescription());
        dateListItem.setImage(item.getWeather().get(0).getIcon());
        dateListItem.setTemperature(item.getMain().getTemp());
        dateListItem.setTemperatureMin(item.getMain().getTempMin());
        dateListItem.setTemperatureMax(item.getMain().getTempMax());
        dateListItem.setWindSpeed(item.getWind().getSpeed());
        dateListItem.setCloudsDeg(item.getClouds().getAll());
        dateListItem.setPressure(item.getMain().getPressure());
        dateListItem.setHumidity(item.getMain().getHumidity());
        dateListItem.setRain3h(item.getRain() != null ? item.getRain().getJsonMember3h() : 0);

        return dateListItem;
    }
}
