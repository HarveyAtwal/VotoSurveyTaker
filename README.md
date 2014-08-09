Voto-SurveyTaker
================

How to run "Voto Survey Taker"
------------------------------
	1. Check-out the following packages
	
		- Android-support-v7-appcompat
		- Actionbar-PullToRefresh
		- Actionbar-PullToRefresh-appcompat
		- SmoothProgressBar
		- google-play-services_lib
		
		-------Found in shared SVN----------
		- Volley
		- VotoProxyTaker
		
	2. Right click SmoothProgressBar
		- Click "properties"
		- Go to "Android" section
		- Make sure "Is Library" is checked
		
	3. Right click "Actionbar-PullToRefresh"
		- Click "properties"
		- Go to "Android" section
		- Add "SmoothProgressBar" to libraries
	
	4. Right click "Actionbar-PullToRefresh-appcompat"
		- Click "properties"
		- Go to "Android" section
		- Add the following libraries
			a. Actionbar-PullToRefresh
			b. Android-support-v7-appcompat
			
	5. Right click "VotoSurveyTaker" application
		- Click "properties"
		- Go to "Android" section
		- Add the following libraries
			a. Volley
			b. VotoProxyTaker
			c. ActionBar-PullToRefresh-appcompat
			d. Android-support-v7-appcompat
			e. google-play-services_lib
