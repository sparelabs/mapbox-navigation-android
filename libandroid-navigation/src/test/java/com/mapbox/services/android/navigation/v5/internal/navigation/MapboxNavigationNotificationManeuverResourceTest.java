package com.mapbox.services.android.navigation.v5.internal.navigation;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.api.directions.v5.DirectionsAdapterFactory;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.BannerText;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.api.directions.v5.models.StepManeuver;
import com.mapbox.services.android.navigation.v5.BaseTest;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationConstants;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Locale;
import java.util.Objects;

import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_LEFT;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_RIGHT;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_SHARP_LEFT;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_SHARP_RIGHT;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_SLIGHT_LEFT;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_STRAIGHT;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_MODIFIER_UTURN;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_TYPE_ARRIVE;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_TYPE_DEPART;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_TYPE_END_OF_ROAD;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_TYPE_FORK;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_TYPE_MERGE;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_TYPE_OFF_RAMP;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_TYPE_ROUNDABOUT;
import static com.mapbox.services.android.navigation.v5.navigation.NavigationConstants.STEP_MANEUVER_TYPE_TURN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("KotlinInternalInJava")
public class MapboxNavigationNotificationManeuverResourceTest extends BaseTest {

  private static final String DIRECTIONS_ROUTE_FIXTURE = "directions_v5_precision_6.json";
  private DirectionsRoute route;

  @Before
  public void setUp() throws Exception {
    final String json = loadJsonFixture(DIRECTIONS_ROUTE_FIXTURE);
    Gson gson = new GsonBuilder()
      .registerTypeAdapterFactory(DirectionsAdapterFactory.create()).create();
    DirectionsResponse response = gson.fromJson(json, DirectionsResponse.class);
    route = response.routes().get(0);
  }

  @Test
  public void checksManeuverResourceIsRetrievedForTurnUturnRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_TURN, STEP_MANEUVER_MODIFIER_UTURN,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertTrue(mapboxNavigationNotification.getCurrentManeuverType() != null);
  }

  @Test
  public void checksManeuverResourceIsRetrievedForTurnUturnLeftDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_TURN, STEP_MANEUVER_MODIFIER_UTURN,
      STEP_MANEUVER_MODIFIER_LEFT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_TURN, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForArriveLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ARRIVE, STEP_MANEUVER_MODIFIER_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ARRIVE, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForArriveRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ARRIVE, STEP_MANEUVER_MODIFIER_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ARRIVE, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForArriveEmptyRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ARRIVE, "",
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ARRIVE, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForDepartLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_DEPART, STEP_MANEUVER_MODIFIER_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_DEPART, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForDepartRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_DEPART, STEP_MANEUVER_MODIFIER_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_DEPART, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForDepartEmptyRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_DEPART, "",
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_DEPART, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForTurnSharpRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_TURN, STEP_MANEUVER_MODIFIER_SHARP_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_TURN, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForTurnRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_TURN, STEP_MANEUVER_MODIFIER_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_TURN, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForTurnSlightRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_TURN, STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_TURN, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForTurnSharpLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_TURN, STEP_MANEUVER_MODIFIER_SHARP_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_TURN, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForTurnLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_TURN, STEP_MANEUVER_MODIFIER_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_TURN, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForTurnSlightLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_TURN, STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_TURN, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForMergeLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_MERGE, STEP_MANEUVER_MODIFIER_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_MERGE, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForMergeRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_MERGE, STEP_MANEUVER_MODIFIER_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_MERGE, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForOffRampLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_OFF_RAMP, STEP_MANEUVER_MODIFIER_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_OFF_RAMP, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForOffRampSlightLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_OFF_RAMP, STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_OFF_RAMP, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForOffRampRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_OFF_RAMP, STEP_MANEUVER_MODIFIER_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_OFF_RAMP, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForOffRampSlightRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_OFF_RAMP, STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_OFF_RAMP, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForForkLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_FORK, STEP_MANEUVER_MODIFIER_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_FORK, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForForkSlightLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_FORK, STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_FORK, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForForkRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_FORK, STEP_MANEUVER_MODIFIER_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_FORK, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForForkSlightRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_FORK, STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_FORK, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForForkStraightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_FORK, STEP_MANEUVER_MODIFIER_STRAIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_FORK, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForForkEmptyRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_FORK, "",
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_FORK, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForEndRoadLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_END_OF_ROAD, STEP_MANEUVER_MODIFIER_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_END_OF_ROAD, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForEndRoadRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_END_OF_ROAD, STEP_MANEUVER_MODIFIER_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_END_OF_ROAD, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutSharpLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_SHARP_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutSlightLeftRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutSharpRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_SHARP_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutSlightRightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutStraightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_STRAIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutEmptyRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, "",
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutLeftLeftDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_LEFT,
      STEP_MANEUVER_MODIFIER_LEFT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutSharpLeftLeftDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_SHARP_LEFT,
      STEP_MANEUVER_MODIFIER_LEFT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutSlightLeftLeftDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_SLIGHT_LEFT,
      STEP_MANEUVER_MODIFIER_LEFT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutRightLeftDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_RIGHT,
      STEP_MANEUVER_MODIFIER_LEFT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutSharpRightLeftDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_SHARP_RIGHT,
      STEP_MANEUVER_MODIFIER_LEFT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutSlightRightLeftDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_SLIGHT_RIGHT,
      STEP_MANEUVER_MODIFIER_LEFT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForRoundaboutStraightLeftDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_ROUNDABOUT, STEP_MANEUVER_MODIFIER_STRAIGHT,
      STEP_MANEUVER_MODIFIER_LEFT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_ROUNDABOUT, mapboxNavigationNotification.getCurrentManeuverType());
  }

  @Test
  public void checksManeuverResourceIsRetrievedForMergeStraightRightDrivingSide() {
    MapboxNavigationNotification mapboxNavigationNotification = buildMapboxNavigationNotification();
    LegStep step = buildLegStep(STEP_MANEUVER_TYPE_MERGE, STEP_MANEUVER_MODIFIER_STRAIGHT,
      STEP_MANEUVER_MODIFIER_RIGHT);
    RouteProgress mockedRouteProgress = buildMockedRouteProgress(step);

    mapboxNavigationNotification.updateNotificationViews(mockedRouteProgress);

    assertEquals(STEP_MANEUVER_TYPE_MERGE, mapboxNavigationNotification.getCurrentManeuverType());
  }

  private MapboxNavigationNotification buildMapboxNavigationNotification() {
    MapboxNavigation mockedMapboxNavigation = createMapboxNavigation();
    Context mockedContext = createContext();
    Notification mockedNotification = mock(Notification.class);
    MapboxNavigationNotification notification = new MapboxNavigationNotification(mockedContext,
            mockedMapboxNavigation, mockedNotification);

    MapboxNavigationNotification spyNotification = Mockito.spy(notification);
    doReturn(null).when(spyNotification).getManeuverBitmap(anyString(), anyString(), anyString(), anyFloat());
    return spyNotification;
  }

  private MapboxNavigation createMapboxNavigation() {
    MapboxNavigation mockedMapboxNavigation = mock(MapboxNavigation.class);
    when(mockedMapboxNavigation.getRoute()).thenReturn(route);
    MapboxNavigationOptions mockedMapboxNavigationOptions = mock(MapboxNavigationOptions.class);
    when(mockedMapboxNavigation.options()).thenReturn(mockedMapboxNavigationOptions);
    when(mockedMapboxNavigationOptions.roundingIncrement()).thenReturn(NavigationConstants.ROUNDING_INCREMENT_FIVE);
    return mockedMapboxNavigation;
  }

  private Context createContext() {
    Context mockedContext = mock(Context.class);
    Configuration mockedConfiguration = new Configuration();
    mockedConfiguration.locale = new Locale("en");
    Resources mockedResources = mock(Resources.class);
    when(mockedContext.getResources()).thenReturn(mockedResources);
    when(mockedResources.getConfiguration()).thenReturn(mockedConfiguration);
    PackageManager mockedPackageManager = mock(PackageManager.class);
    when(mockedContext.getPackageManager()).thenReturn(mockedPackageManager);
    when(mockedContext.getString(anyInt())).thenReturn("%s ETA");
    return mockedContext;
  }

  private RouteProgress buildMockedRouteProgress(LegStep step) {
    RouteProgress mockedRouteProgress = mock(RouteProgress.class, RETURNS_DEEP_STUBS);
    when(mockedRouteProgress.currentLegProgress().upComingStep()).thenReturn(step);
    return mockedRouteProgress;
//    when(Objects.requireNonNull(mockedRouteProgress.currentLegProgress()).upComingStep()).thenReturn(step);
//
//    return mockedRouteProgress.toBuilder()
//            .bannerInstruction(buildBannerInstructions(step.maneuver().type(),
//                    step.maneuver().modifier()))
//            .build();
  }

  private BannerInstructions buildBannerInstructions(String maneuverType, String maneuverModifier) {
    BannerText bannerText = mock(BannerText.class);
    when(bannerText.text()).thenReturn("mock text");
    when(bannerText.type()).thenReturn(maneuverType);
    when(bannerText.modifier()).thenReturn(maneuverModifier);
    return BannerInstructions.builder()
            .primary(bannerText)
            .distanceAlongGeometry(0.3f)
            .build();
  }

  private LegStep buildLegStep(String type, String modifier, String drivingSide) {
    LegStep.Builder legStepBuilder = LegStep.fromJson("{\"distance\":56.4,\"duration\":7.5," +
      "\"geometry\":\"ec{bcBjdeqBmByByAaDgAoFWgGXgGhAoF|@wB\",\"name\":\"Saltley Road (A47)\",\"ref\":\"A47\"," +
      "\"mode\":\"driving\",\"maneuver\":{\"location\":[-1.870934,52.492355],\"bearing_before\":60.0," +
      "\"bearing_after\":35.0,\"instruction\":\"Enter the traffic circle and take the 2nd exit onto Saltley Road " +
      "(A47)\",\"type\":\"roundabout\",\"modifier\":\"slight left\",\"exit\":2}," +
      "\"voiceInstructions\":[{\"distanceAlongGeometry\":56.4,\"announcement\":\"Exit the traffic circle onto Saltley" +
      " Road (A47)\",\"ssmlAnnouncement\":\"\\u003cspeak\\u003e\\u003camazon:effect " +
      "name\\u003d\\\"drc\\\"\\u003e\\u003cprosody rate\\u003d\\\"1.08\\\"\\u003eExit the traffic circle onto Saltley" +
      " Road (\\u003csay-as interpret-as\\u003d\\\"address\\\"\\u003eA47\\u003c/say-as\\u003e)" +
      "\\u003c/prosody\\u003e\\u003c/amazon:effect\\u003e\\u003c/speak\\u003e\"}]," +
      "\"bannerInstructions\":[{\"distanceAlongGeometry\":56.4,\"primary\":{\"text\":\"Saltley Road\"," +
      "\"components\":[{\"text\":\"Saltley Road\",\"type\":\"text\",\"abbr\":\"Saltley Rd\",\"abbr_priority\":0}]," +
      "\"type\":\"roundabout\",\"modifier\":\"left\",\"degrees\":290.0,\"driving_side\":\"left\"}," +
      "\"secondary\":{\"text\":\"A47\",\"components\":[{\"text\":\"A47\",\"type\":\"icon\"}],\"type\":\"roundabout\"," +
      "\"modifier\":\"left\"},\"sub\":{\"text\":\"Heartlands Parkway\",\"components\":[{\"text\":\"Heartlands " +
      "Parkway\",\"type\":\"text\",\"abbr\":\"Heartlands Pky\",\"abbr_priority\":0}],\"type\":\"roundabout\"," +
      "\"modifier\":\"left\",\"degrees\":174.0,\"driving_side\":\"left\"}}],\"driving_side\":\"left\",\"weight\":8.6," +
      "\"intersections\":[{\"location\":[-1.870934,52.492355],\"bearings\":[30,195,240],\"entry\":[true,false,false]," +
      "\"in\":2,\"out\":0},{\"location\":[-1.870792,52.492455],\"bearings\":[30,60,225],\"entry\":[true,true,false]," +
      "\"in\":2,\"out\":1},{\"location\":[-1.870288,52.492453],\"bearings\":[135,300,345],\"entry\":[true,false," +
      "false],\"in\":1,\"out\":0}]}").toBuilder();
    StepManeuver stepManeuver = legStepBuilder.build().maneuver().toBuilder()
      .type(type)
      .modifier(modifier)
      .build();
    legStepBuilder
      .maneuver(stepManeuver)
      .drivingSide(drivingSide);

    return legStepBuilder.build();
  }
}
