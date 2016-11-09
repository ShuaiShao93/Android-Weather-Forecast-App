<?php
    $GOOGLE_API_KEY = "AIzaSyCxBKxShIpFVy0FdIze_ld4ISoQNrjWdc0";
    $url1 = "https://maps.googleapis.com/maps/api/geocode/xml?address=".urlencode($_GET["address"]).','.urlencode($_GET["city"]).','.$_GET["state"].'&key='.$GOOGLE_API_KEY;
    $xmlFile = file_get_contents($url1);
    $xmlDoc = simplexml_load_string($xmlFile);
    $loc = $xmlDoc->result->geometry->location;
    $lat = $loc->lat;
    $lng = $loc->lng;
    
    if ($_GET["degree"] == "Fahrenheit"):
        $units = "us";
        $temp_units = "&#8457;";
        $windSpd_units = " mph";
        $visibility_units = " mi";
        $pressure_units = " mb";
    elseif ($_GET["degree"] == "Celsius"):
        $units = "si";
        $temp_units = "&#8451;";
        $windSpd_units = " m/s";
        $visibility_units = " km";
        $pressure_units = " hPa";
    endif;

    $FORECAST_API_KEY = "442a1def952957db9e2fbd9ab98a2f0a";
    $url2 = "https://api.forecast.io/forecast/".$FORECAST_API_KEY."/$lat,$lng?units=$units&exclude=flags";
    $jsonFile = file_get_contents($url2);
    $json = json_decode($jsonFile,true);

    function icon_url ($icon){
        $image_url = "";
        if($icon=="clear-day"){$image_url .= "clear";} 
        elseif($icon=="clear-night"){$image_url .= "clear_night";} 
        elseif($icon=="partly-cloudy-day"){$image_url .= "cloud_day";}
        elseif($icon=="partly-cloudy-night"){$image_url .= "cloud_night";} 
        else{$image_url .= $icon;}
        
        return $image_url;
    }

    function state_convert($state_name){
        $state_map = array();
        $state_map["AL"]="Alabama";
        $state_map["AK"]="Alaska";
        $state_map["AZ"]="Arizona";
        $state_map["AR"]="Arkansas";
        $state_map["CA"]="California";
        $state_map["CO"]="Colorado";
        $state_map["CT"]="Connecticut";
        $state_map["DE"]="Delaware";
        $state_map["DC"]="District Of Columbia";
        $state_map["FL"]="Florida";
        $state_map["GA"]="Georgia";
        $state_map["HI"]="Hawaii";
        $state_map["ID"]="Idaho";
        $state_map["IL"]="Illinois";
        $state_map["IN"]="Indiana";
        $state_map["IA"]="Iowa";
        $state_map["KS"]="Kansas";
        $state_map["KY"]="Kentucky";
        $state_map["LA"]="Louisiana";
        $state_map["ME"]="Maine";
        $state_map["MD"]="Maryland";
        $state_map["MA"]="Massachusetts";
        $state_map["MI"]="Michigan";
        $state_map["MN"]="Minnesota";
        $state_map["MS"]="Mississippi";
        $state_map["MO"]="Missouri";
        $state_map["MT"]="Montana";
        $state_map["NE"]="Nebraska";
        $state_map["NV"]="Nevada";
        $state_map["NH"]="New Hampshire";
        $state_map["NJ"]="New Jersey";
        $state_map["NM"]="New Mexico";
        $state_map["NY"]="New York";
        $state_map["NC"]="North Carolina";
        $state_map["ND"]="North Dakota";
        $state_map["OH"]="Ohio";
        $state_map["OK"]="Oklahoma";
        $state_map["OR"]="Oregon";
        $state_map["PA"]="Pennsylvania";
        $state_map["RI"]="Rhode Island";
        $state_map["SC"]="South Carolina";
        $state_map["SD"]="South Dakota";
        $state_map["TN"]="Tennessee";
        $state_map["TX"]="Texas";
        $state_map["UT"]="Utah";
        $state_map["VT"]="Vermont";
        $state_map["VA"]="Virginia";
        $state_map["WA"]="Washington";
        $state_map["WV"]="West Virginia";
        $state_map["WI"]="Wisconsin";
        $state_map["WY"]="Wyoming";
        
        return array_search($state_name, $state_map);
    }
    
    $output = array(array(), array(), array(), array());

    $output[3]['longitude'] = (float)$lng;
    $output[3]['latitude'] = (float)$lat;

    $icon = $json['currently']['icon'];
    $output[0]['icon_url'] = icon_url($icon);
    $output[0]['summary'] = $json['currently']['summary'];
    $output[0]['location'] = $_GET['city'].', '.state_convert($_GET['state']);
    $output[0]['temp'] = (int)$json['currently']['temperature'];
    $output[0]['temp_units'] = $temp_units;
    $output[0]['minTemp'] = (int)$json['daily']['data'][0]['temperatureMin'];
    $output[0]['maxTemp'] = (int)$json['daily']['data'][0]['temperatureMax'];
    
    if (isset($json["currently"]["precipIntensity"])){
        $precipIntensity=$json["currently"]["precipIntensity"]; 
        if($units == "si"){$precipIntensity /= 25.4;}
        if($precipIntensity>=0 and $precipIntensity<0.002){$precip = "None";} 
        elseif($precipIntensity>=0.002 and $precipIntensity<0.017){$precip = "Very Light";} 
        elseif($precipIntensity>=0.017 and $precipIntensity<0.1){$precip = "Light";} 
        elseif($precipIntensity>=0.1 and $precipIntensity<0.4){$precip = "Moderate";} 
        elseif($precipIntensity>=0.4){$precip = "Heavy";}
        $output[0]['precipitation'] = $precip;
    }
    else{
        $output[0]['precipitation'] = "N.A.";
    }
    
    if (isset($json["currently"]["precipProbability"])){
        $precipProbability = $json['currently']['precipProbability']*100;
        $output[0]['chanceofRain'] = (int)$precipProbability."%";
    }
    else{
        $output[0]['chanceofRain'] = "N.A.";
    }
    if (isset($json["currently"]["windSpeed"])){
        $output[0]['windSpeed'] = round($json['currently']['windSpeed'], 2).$windSpd_units;
    }
    else{
        $output[0]['windSpeed'] = "N.A.";
    }
    if (isset($json["currently"]["dewPoint"])){
        $output[0]['dewPoint'] = round($json['currently']['dewPoint'], 2);
    }
    else {
        $output[0]['dewPoint'] = "N.A.";
    }
    if (isset($json["currently"]["humidity"])){
        $humidity = $json['currently']['humidity']*100;
        $output[0]['humidity'] = (int)$humidity."%";
    }
    else{
        $output[0]['humidity'] = "N.A.";
    }
    if (isset($json["currently"]["visibility"])){
        $output[0]['visibility'] = round($json['currently']['visibility'],2).$visibility_units;
    }
    else{
        $output[0]['visibility'] = "N.A.";
    }

    date_default_timezone_set($json["timezone"]);
    if (isset($json["daily"]["data"][0]["sunriseTime"])){
        $sunrise_timestamp=$json["daily"]["data"][0]["sunriseTime"]; 
        $sunrise_time=date("h:i A", $sunrise_timestamp);
        $output[0]['Sunrise'] = $sunrise_time;
    }
    else{
        $output[0]['Sunrise'] = "N.A.";
    }
    if (isset($json["daily"]["data"][0]["sunsetTime"])){
        $sunset_timestamp=$json["daily"]["data"][0]["sunsetTime"]; 
        $sunset_time=date("h:i A", $sunset_timestamp);
        $output[0]['Sunset'] = $sunset_time;
    }
    else{
        $output[0]['Sunset'] = "N.A.";
    }

    for ($h = 0; $h < 48; $h ++){
        $output[1][$h] = array();
        $timestamp = $json["hourly"]["data"][$h]["time"];
        $output[1][$h]["time"] = date("h:i A", $timestamp);
        $output[1][$h]["icon_url"] = icon_url($json["hourly"]["data"][$h]["icon"]);
        if (isset($json["hourly"]["data"][$h]["cloudCover"])){
            $cloudCover = $json["hourly"]["data"][$h]["cloudCover"] * 100;
            $output[1][$h]["cloudCover"] = (int)$cloudCover . "%";
        }
        else{
            $output[1][$h]["cloudCover"] = "N.A.";
        }
        $output[1][$h]["temp"] = (int)$json["hourly"]["data"][$h]["temperature"];
        $output[1][$h]["temp_units"] = $temp_units;
        if (isset($json["hourly"]["data"][$h]["windSpeed"])){
            $output[1][$h]["windSpeed"] = round($json["hourly"]["data"][$h]["windSpeed"], 2).$windSpd_units;
        }
        else{
            $output[1][$h]["windSpeed"] = "N.A.";
        }
        if (isset($json["hourly"]["data"][$h]["humidity"])){
            $humidity = $json["hourly"]["data"][$h]["humidity"] * 100;
            $output[1][$h]["humidity"] = (int)$humidity."%";
        }
        else{
            $output[1][$h]["humidity"] = "N.A.";
        }
        if (isset($json["hourly"]["data"][$h]["visibility"])){
            $output[1][$h]["visibility"] = round($json["hourly"]["data"][$h]["visibility"], 2).$visibility_units;
        }
        else{
            $output[1][$h]["visibility"] = "N.A.";
        }
        if (isset($json["hourly"]["data"][$h]["pressure"])){
            $output[1][$h]["pressure"] = round($json["hourly"]["data"][$h]["pressure"], 2).$pressure_units;
    }
        else{
            $output[1][$h]["pressure"] = "N.A.";
        }
    }

    for ($d = 0; $d <= 7; $d ++){
        $output[2][$d] = array();
        $timestamp = $json["daily"]["data"][$d]["time"];
        $output[2][$d]["weekday"] = date("l", $timestamp);
        $output[2][$d]["date"] = date("M d", $timestamp);
        $output[2][$d]["icon_url"] = icon_url($json["daily"]["data"][$d]["icon"]);
        $output[2][$d]["minTemp"] = (int)$json["daily"]["data"][$d]["temperatureMin"];
        $output[2][$d]["maxTemp"] = (int)$json["daily"]["data"][$d]["temperatureMax"];
        $output[2][$d]["city"] = $_GET["city"];
        $output[2][$d]["summary"] = $json["daily"]["data"][$d]["summary"];
        
        if (isset($json["daily"]["data"][$d]["sunriseTime"])){
            $sunrise_timestamp=$json["daily"]["data"][$d]["sunriseTime"]; 
            $sunrise_time=date("h:i A", $sunrise_timestamp);
            $output[2][$d]['Sunrise'] = $sunrise_time;
        }
        else{
            $output[2][$d]['Sunrise'] = "N.A.";
        }
        if (isset($json["daily"]["data"][$d]["sunsetTime"])){
            $sunset_timestamp=$json["daily"]["data"][$d]["sunsetTime"]; 
            $sunset_time=date("h:i A", $sunset_timestamp);
            $output[2][$d]['Sunset'] = $sunset_time;
        }
        else{
            $output[2][$d]['Sunset'] = "N.A.";
        }
        
        if (isset($json["daily"]["data"][$d]["humidity"])){
            $humidity = $json["daily"]["data"][$d]["humidity"] * 100;
            $output[2][$d]["humidity"] = (int)$humidity."%";
        }
        else{
            $output[2][$d]["humidity"] = "N.A.";
        }
        if (isset($json["daily"]["data"][$d]["windSpeed"])){
            $output[2][$d]["windSpeed"] = round($json["daily"]["data"][$d]["windSpeed"], 2).$windSpd_units;
        }
        else{
            $output[2][$d]["windSpeed"] = "N.A.";
        }
        if (isset($json["daily"]["data"][$d]["visibility"])){
            $output[2][$d]["visibility"] = round($json["daily"]["data"][$d]["visibility"], 2).$visibility_units;
        }
        else{
            $output[2][$d]["visibility"] = "N.A.";
        }
        if (isset($json["daily"]["data"][$d]["pressure"])){
            $output[2][$d]["pressure"] = round($json["daily"]["data"][$d]["pressure"], 2).$pressure_units;
        }
        else{
            $output[2][$d]["pressure"] = "N.A.";
        }
    }

    echo json_encode($output);
?>