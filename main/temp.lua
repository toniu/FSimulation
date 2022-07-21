local RSSSimulation = {}

local player = game.Players.LocalPlayer
local start = player.PlayerGui:WaitForChild("Start")
--[[ Match Elements ]]--
local MM = player.PlayerGui:WaitForChild("MatchManager")
local SFX = MM:WaitForChild("Sounds")
local SB = MM:WaitForChild("SB")
--[[ GUI Elements ]]--
local match = start:WaitForChild("Match")
local storage = match:WaitForChild("Storage")
local kickOffBtn = match:WaitForChild("KickOff")
local resumeBtn = match:WaitForChild("Resume")
local finishSeason = match:WaitForChild("FinishSeason")

local HGoalMsg = storage:WaitForChild("HGoalEvent")
local AGoalMsg = storage:WaitForChild("AGoalEvent")
local halfEndMsg = storage:WaitForChild("Halfend")
local playedEvent = storage:WaitForChild("REvent")
local quickRun = false
--
local events = match:WaitForChild("Events")

--[[ SB Formats]]--
local scoreFormat = "%d - %d";
local timeFormats = {
	secondsLeadingZero = "%d:0%d",
	minutesLeadingZero = "0%d:%d",
	bothLeadingZero = "0%d:0%d",
	default = "%d:%d",
};
local formatType = timeFormats.secondsLeadingZero

--[[ SB Elements ]]--
local SBGUI = match:WaitForChild("SB")
local SBHTeam = SBGUI:WaitForChild("HTeam")
local homeSBColor = SBHTeam:WaitForChild("HColor")
local homeName = SBHTeam:WaitForChild("HTName")

local SBATeam = SBGUI:WaitForChild("ATeam")
local awaySBColor = SBATeam:WaitForChild("AColor")
local awayName = SBATeam:WaitForChild("ATName")

local SBScore = SBGUI:WaitForChild("Score")
local SBTime = SBGUI:WaitForChild("Time")

--[[ SB Variables ]]--
local HScore = SB:WaitForChild("HScore").Value
local AScore = SB:WaitForChild("AScore").Value
local half = SB:WaitForChild("Half").Value
local timer = SB:WaitForChild("Timer").Value

--[[ Team Variables ]]--
local homeTeam = nil
local awayTeam = nil
local HAttempts = 0
local AAttempts = 0

local isPlaying = false


---[[ Event messages ]]---
local teamMentioned = ""
local playerMentioned = ""

--- Open play Possession gained:
local possessionGained = {
	"",
	"",
}
--- Open play Possession lost:
local possessionLost = {
	"",
	"",
}
--- Open Chance created:
local openChanceCreated = {
	"",
	"",
	"",
	"",
}
--- Open Chance converted:
local openChanceConverted = {
	"",
	"",
}
--- Open Chance missed:
local openChanceMissed = {
	"",
	"",
}
--- Set-piece chance created:
local freekickCreated = {
	"",
	"",
}

local penaltyCreated = {
	"",
	"",
}

local cornerCreated = {
	"",
	"",
}
--- Set-piece converted:
local freekickConverted = {
	"",
	"",
}

local penaltyConverted = {
	"",
	"",
}

local cornerConverted = {
	"",
	"",
}
--- Set-piece missed:
local freekickMissed = {
	"",
	"",
}

local penaltyMissed = {
	"",
	"",
}

local cornerFailed = {
	"",
	"",
}

--[[ Match Settings ]]--

local focusPointer = 1
local focus = {
	"Midfield",
	"Defence",
	"Attack"
}

local MSPointer = 1
local matchSpeeds = {
	0.75,
	0.35,
	0.125
} 

function RSSSimulation.isMatchPlaying()
	return isPlaying
end

function RSSSimulation.setFocus()
	focusPointer = focusPointer + 1
	if focusPointer > 3 then
		focusPointer = 1
	end
	
	SB:WaitForChild("Focus").Value = focus[focusPointer]
end

function RSSSimulation.setMatchSpeed()
	MSPointer = MSPointer + 1
	if MSPointer > 3 then
		MSPointer = 1
	end
	
	SB:WaitForChild("MatchSpeed").Value = matchSpeeds[MSPointer]
end

--[[ Match Functions ]] --
function RSSSimulation.setTeams(homeT, awayT)
	local HColor = homeT.General:WaitForChild("TeamColor").Value
	local AColor = awayT.General:WaitForChild("TeamColor").Value
	
	local HCode = homeT.General:WaitForChild("Abbreviation").Value
	local ACode = awayT.General:WaitForChild("Abbreviation").Value
	
	homeSBColor.BackgroundColor3 = HColor.Color
	awaySBColor.BackgroundColor3 = AColor.Color
	
	homeName.Text = HCode
	awayName.Text = ACode
end

function RSSSimulation.clearEvents()
	for i, v in pairs (events:GetChildren()) do
		if v:IsA("Frame") then
			v:Destroy()
		end
	end
end

function RSSSimulation.resetScore()
	SB:WaitForChild("HScore").Value = 0
	SB:WaitForChild("AScore").Value = 0

	SBScore.Text = string.format(scoreFormat, SB.HScore.Value, SB.AScore.Value);
end

function RSSSimulation.newEvent(minsPassed, gameState, showClock, half, teamColor)
	print(gameState)
	-- timer correction
	local clockMsg = tostring(minsPassed) .. "'"
	if minsPassed > 45 and half == 1 then
		clockMsg = tostring(minsPassed) .. "+" tostring(minsPassed - 45)
	elseif minsPassed > 90 and half == 2 then
		clockMsg = tostring(minsPassed) .. "+" tostring(minsPassed - 90)
	elseif minsPassed > 105 and half == 3 then
		clockMsg = tostring(minsPassed) .. "+" tostring(minsPassed - 105)
	elseif minsPassed > 120 and half == 4 then
		clockMsg = tostring(minsPassed) .. "+" tostring(minsPassed - 120)
	end
	
	-- clone new event
	local event = nil
	if gameState == "HGoal" then
		event = HGoalMsg:Clone()
		event.Parent = events
		event.Visible = true
		
		local FIL = event:WaitForChild("FIL")
		local moveBG = FIL:FindFirstChild("MovingBG")
		if moveBG then
			moveBG.Disabled = false
		end
	elseif gameState == "AGoal" then
		event = AGoalMsg:Clone()
		event.Parent = events
		event.Visible = true
		
		local FIL = event:WaitForChild("FIL")
		local moveBG = FIL:FindFirstChild("MovingBG")
		if moveBG then
			moveBG.Disabled = false
		end
	elseif gameState == "HT" or gameState == "FT" or gameState == "Kick Off" then
		-- kick off, half time, full time, 
		event = halfEndMsg:Clone()
		event.Name = clockMsg
		
		local eventMsg = event:WaitForChild("EventMsg")
		if half == 1 then
			eventMsg.Text = "KICK OFF"
		elseif half == 2 then
			eventMsg.Text = "HALF TIME"
		elseif half == 3 then
			eventMsg.Text = "FULL TIME"
		elseif half == 4 then
			eventMsg.Text = "EXTRA TIME HALF TIME"
		else
			eventMsg.Text = "PENALTIES"
		end
		
		event.Parent = events
		event.Visible = true
		return 
	else -- resumed match
		event = playedEvent:Clone()
		event.Name = clockMsg
		
		local eventMsg = event:WaitForChild("EventMsg")
		eventMsg.Text = gameState
		
		if teamColor ~= nil then
			event:WaitForChild("FIL").ImageColor3 = teamColor.Color
		end
		
		event.Parent = events
		event.Visible = true
	end
	
	local eventTimestamp = event:WaitForChild("Timestamp")
	eventTimestamp.Text = clockMsg

	if not showClock then 
		eventTimestamp.Visible = false
	end
end

function RSSSimulation.startGame(MMatch, isQuickRun)
	
	homeTeam = MMatch:WaitForChild("homeTeam").General:WaitForChild("Pointer").Value
	awayTeam = MMatch:WaitForChild("awayTeam").General:WaitForChild("Pointer").Value
	
	quickRun = isQuickRun
	
	RSSSimulation.clearEvents()
	isPlaying = true
	kickOffBtn.Visible = false
	RSSSimulation.resetScore()
	
	--RSSSimulation.setTeams(homeTeam, awayTeam)
	
	--[[ Match / Timer Variables ]]--
	local HScore = 0
	local AScore = 0
	
	local homeColor = homeTeam.General.TeamColor.Value
	local awayColor = awayTeam.General.TeamColor.Value
	local matchState = ""
	
	--[[ Team Variables ]]--
	
	MM:WaitForChild("HTeam").Value = homeTeam
	MM:WaitForChild("ATeam").Value = awayTeam
	
	local homeAttack = homeTeam.Ratings:WaitForChild("Attack").Value
	local homeMidfield = homeTeam.Ratings:WaitForChild("Midfield").Value
	local homeDefence = homeTeam.Ratings:WaitForChild("Defence").Value
	local homeOVR = math.floor((homeAttack + homeMidfield + homeDefence) / 3)
	
	local awayAttack = awayTeam.Ratings:WaitForChild("Attack").Value
	local awayMidfield = awayTeam.Ratings:WaitForChild("Midfield").Value
	local awayDefence = awayTeam.Ratings:WaitForChild("Defence").Value
	local awayOVR =  math.floor((awayAttack + awayMidfield + awayDefence) / 3)
	
	
	
	----- Advantage factors
	if (homeOVR > awayOVR) then
		-- home overall advantage
		homeOVR = math.floor(homeOVR + (15 * (awayOVR / homeOVR)))
		math.clamp(homeOVR, 0, 95)
		
		awayOVR = math.floor(awayOVR - (15 * (awayOVR / homeOVR)))
		math.clamp(awayOVR, 0, 95)
	elseif (awayOVR > homeOVR) then
		-- away overall advantage
		awayOVR = math.floor(awayOVR + (10 * (homeOVR / awayOVR)))
		math.clamp(awayOVR, 0, 95)
		
		homeOVR = math.floor(homeOVR - (10 * (homeOVR / awayOVR)))
		math.clamp(homeOVR, 0, 95)
	end
	
	
	if (homeAttack > awayDefence) then
		-- home attack advantage
		homeAttack = math.floor(homeAttack + (15 * (awayDefence / homeAttack)))
		math.clamp(homeAttack, 0, 95)
		
		awayDefence = math.floor(awayDefence - (15 * (awayDefence / homeAttack)))
		math.clamp(awayDefence, 0, 95)
	elseif (awayDefence > homeAttack) then
		-- away defence advantage
		awayDefence = math.floor(awayDefence + (20 * (homeAttack / awayDefence)))
		math.clamp(awayDefence, 0, 95)
		
		homeAttack = math.floor(homeAttack - (10 * (homeAttack / awayDefence)))
		math.clamp(homeAttack, 0, 95)
	end
	
	if (awayAttack > homeDefence) then
		-- away attack advantage
		awayAttack = math.floor(awayAttack + (10 * (homeDefence / awayAttack)))
		math.clamp(awayAttack, 0, 95)
		
		homeDefence = math.floor(homeDefence - (10 * (homeDefence / awayAttack)))
		math.clamp(homeDefence, 0, 95)
	elseif (homeDefence > awayAttack) then
		-- home defence advantage
		homeDefence = math.floor(homeDefence + (25 * (awayAttack / homeDefence)))
		math.clamp(homeDefence, 0, 95)
		
		awayAttack = math.floor(awayAttack - (15 * (awayAttack / homeDefence)))
		math.clamp(awayAttack, 0, 95)
	end
		
	
	
	local RNG = Random.new()
	SB:WaitForChild("Timer").Value = 0
	local clock = SB.Timer.Value
	
	--[[ Local Functions ]]--
	--- determines if there is going to be action in the minute
	local function beginAction()
		local o = RNG:NextNumber(1,200)
		if o <= 60 then
			return true
		else
			return false
		end
	end
	
	--- determines if a chance will be converted whether that is via open-play or set-piece
	local function createChance(side, team)
		if side == "H" then
			
			local hLimit = math.floor((homeAttack / (awayDefence + homeAttack)) * 100)
			
			local hP = RNG:NextNumber(1, 100)
			if (hP <= hLimit * 0.85) then
				-- goal
				return "HGoal"
			elseif (hP >= hLimit * 1.15) then
				-- no posession
				return "noPos"
			else
				-- set piece created
				local heC = RNG:NextNumber(1,100)
				if heC <= 40 then
					return "HFreekick"
				elseif heC > 40 and heC <= 80 then
					return "HCorner"
				else
					return "HPenalty"
				end
			end
		else
			-- away
			local aLimit = math.floor((awayAttack / (homeDefence + awayAttack)) * 100)
			
			local aP = RNG:NextNumber(1, 100)
			if (aP <= aLimit * 0.85) then
				-- goal
				return "AGoal"
			elseif (aP >= aLimit * 1.15) then
				-- no posession
				return "noPos"
			else
				-- set piece created
				local aeC = RNG:NextNumber(1,100)
				if aeC <= 40 then
					return "AFreekick"
				elseif aeC > 40 and aeC <= 80 then
					return "ACorner"
				else
					return "APenalty"
				end
			end
		end
	end
	
	--- determines if the set-piece will be converted
	local function createSetPiece(side, setpiece)
		if side == "H" then
			--
			if setpiece == "HCorner" then
				--
				local HCScored = RNG:NextNumber(1,100)
				if HCScored <= 15 then
					return "HGoal"
				else
					return "HCornerFailed"
				end
			elseif setpiece == "HFreekick" then
				--
				local HFScored = RNG:NextNumber(1,100)
				if HFScored <= 35 then
					return "HGoal"
				else
					return "HFreekickMiss"
				end
			elseif setpiece == "HPenalty" then
				--
				local HPScored = RNG:NextNumber(1,100)
				if HPScored <= 75 then
					return "HGoal"
				else
					return "HPenaltyMiss"
				end
			end
		else 
			--
			if setpiece == "ACorner" then
				--
				local ACScored = RNG:NextNumber(1,100)
				if ACScored <= 15 then
					return "AGoal"
				else
					return "ACornerFailed"
				end
			elseif setpiece == "AFreekick" then
				--
				local AFScored = RNG:NextNumber(1,100)
				if AFScored <= 35 then
					return "AGoal"
				else
					return "AFreekickMiss"
				end
			elseif setpiece == "APenalty" then
				--
				local APScored = RNG:NextNumber(1,100)
				if APScored <= 75 then
					return "AGoal"
				else
					return "APenaltyMiss"
				end
			end
		end
	end
	
	--- determines whether the posession will develop to a chance created
	local function createBuildup(side, team)
		if side == "H" then
			local hLimit = math.floor(((homeMidfield + homeOVR) / (homeMidfield + awayMidfield + homeOVR + awayOVR)) * 100)
			
			local hPb = RNG:NextNumber(1, 100)
			if (hPb <= hLimit * 0.95) then
				return "HChanceCreated"
			else
				-- no posession
				return "noPos"
			end
		else
			--
			local aLimit = math.floor(((awayMidfield + awayOVR) / (homeMidfield + awayMidfield + homeOVR + awayOVR)) * 100)
			
			local aPb = RNG:NextNumber(1, 100)
			if (aPb <= aLimit * 0.95) then
				return "AChanceCreated"
			else
				-- no posession
				return "noPos"
			end
		end
	end
	
	--- determines which team will gain possession 
	local function gainPossession()
		if homeMidfield >= awayMidfield then
			local hLimit = math.floor(((homeMidfield) / (homeMidfield + awayMidfield)) * 100)
			
			local hPG = RNG:NextNumber(1, 100)
			if (hPG <= hLimit * 0.85) then
				return "hPos"
			elseif (hPG >= hLimit * 1.15) then
				return "aPos"
			else
				-- no posession
				return "noPos"
			end
			--
		else
			local aLimit = math.floor(((awayMidfield) / (homeMidfield + awayMidfield)) * 100)
	
			local aPG = RNG:NextNumber(1, 100)
			if (aPG <= aLimit * 0.85) then
				return "aPos"
			elseif (aPG >= aLimit * 1.15) then
				return "hPos"
			else
				-- no posession
				return "noPos"
			end
			--
		end
	end
	
	--- updates the score of the match
	local function addScore(side)
		if side == "H" then
			SB:WaitForChild("HScore").Value = SB.HScore.Value + 1
			--- update team;s points
			homeTeam.Table:WaitForChild("GF").Value = homeTeam.Table.GF.Value + 1
			homeTeam.Table:WaitForChild("GD").Value = homeTeam.Table.GD.Value + 1
			
			awayTeam.Table:WaitForChild("GA").Value = awayTeam.Table.GA.Value + 1
			awayTeam.Table:WaitForChild("GD").Value = awayTeam.Table.GF.Value - awayTeam.Table.GA.Value
		else
			-- 
			SB:WaitForChild("AScore").Value = SB.AScore.Value + 1
			
			--- update team;s points
			awayTeam.Table:WaitForChild("GF").Value = awayTeam.Table.GF.Value + 1
			awayTeam.Table:WaitForChild("GD").Value = awayTeam.Table.GD.Value + 1
			
			homeTeam.Table:WaitForChild("GA").Value = homeTeam.Table.GA.Value + 1
			homeTeam.Table:WaitForChild("GD").Value = homeTeam.Table.GF.Value - homeTeam.Table.GA.Value
		end
		
		if not quickRun then
			SBScore.Text = string.format(scoreFormat, SB.HScore.Value, SB.AScore.Value);
		end
	end

	local function incrementClock()
		if quickRun then
			clock = clock + 1
			SB:WaitForChild("Timer").Value = clock
			return
		end
		
		local dt = SB:WaitForChild("MatchSpeed").Value / 5
		local minsPassed = clock
		
		for secondsPassed = 0, 59, 5 do
			
			if (minsPassed < 10 and secondsPassed < 10) then
				formatType = timeFormats.bothLeadingZero;
			elseif (minsPassed < 10 and secondsPassed > 10) then
				formatType = timeFormats.minutesLeadingZero;
			elseif (minsPassed > 10 and secondsPassed < 10) then
				formatType = timeFormats.secondsLeadingZero;
			else
				formatType = timeFormats.default;
			end
			
			SBTime.Text = string.format(formatType, minsPassed, secondsPassed);
			wait(dt)
		end
		
		clock = clock + 1
		if (minsPassed < 10) then
			formatType = timeFormats.bothLeadingZero;
		elseif (minsPassed > 10) then
			formatType = timeFormats.secondsLeadingZero;
		else
			formatType = timeFormats.default;
		end
		
		SB:WaitForChild("Timer").Value = clock
		SBTime.Text = string.format(formatType, minsPassed, 0);
	end
	
	local FHstoppageTime = RNG:NextNumber(1,3)
	
	-- firstHalf
	local half = 1
	local dt = SB.MatchSpeed.Value
	
	if quickRun then
		dt = 0.001
	end
	
	matchState = "Kick Off"
	if not quickRun then
		RSSSimulation.newEvent(clock, matchState, true, half, nil)
	end
	
	matchState = "No Possession"
	
	local function matchProgress()		
		dt = SB:WaitForChild("MatchSpeed").Value
		if matchState == "Home Chance" then
			if not quickRun then
				RSSSimulation.newEvent(clock, matchState, false, half, homeColor)
			end
			local HOutcome = createChance("H", homeTeam)
			if HOutcome == "HGoal" then
				matchState = HOutcome
				if not quickRun then
					SFX.Goal:Play()
					RSSSimulation.newEvent(clock, matchState, true, half, nil)
				end
				addScore("H")
			elseif HOutcome == "HFreekick" or HOutcome == "HCorner" or HOutcome == "HPenalty" then
				HAttempts = HAttempts + 1
				matchState = HOutcome
				incrementClock()
				if not quickRun then
					SFX.Whistle:Play()
					RSSSimulation.newEvent(clock, matchState, false, half, homeColor)
				end
				local spOutcome = createSetPiece("H", matchState)
				matchState = spOutcome
				if spOutcome == "HGoal" then
					if not quickRun then
						SFX.Goal:Play()
						RSSSimulation.newEvent(clock, matchState, true, half, nil)
					end
					addScore("H")
				else
					if not quickRun then
						SFX.Miss:Play()
						RSSSimulation.newEvent(clock, matchState, true, half, homeColor)
					end
				end
			else 
				-- chance failed
				matchState = "HChance Missed"
				if not quickRun then
					SFX.Miss:Play()
					RSSSimulation.newEvent(clock, matchState, true, half, homeColor)
				end
			end
		elseif matchState == "Away Chance" then
			if not quickRun then
				RSSSimulation.newEvent(clock, matchState, false, half, awayColor)
			end
			local AOutcome = createChance("A", awayTeam)
			if AOutcome == "AGoal" then
				matchState = AOutcome
				if not quickRun then
					SFX.Goal:Play()
					RSSSimulation.newEvent(clock, matchState, true, half)
				end
				addScore("A")
			elseif AOutcome == "AFreekick" or AOutcome == "ACorner" or AOutcome == "APenalty" then
				AAttempts = AAttempts + 1
				matchState = AOutcome
				incrementClock()
				if not quickRun then
					SFX.Whistle:Play()
					RSSSimulation.newEvent(clock, matchState, false, half, awayColor)
				end
				local spOutcome = createSetPiece("A", matchState)
				matchState = spOutcome
				if spOutcome == "AGoal" then
					if not quickRun then
						SFX.Goal:Play()
						RSSSimulation.newEvent(clock, matchState, true, half)
					end
					addScore("A")
				else
					if not quickRun then
						SFX.Miss:Play()
						RSSSimulation.newEvent(clock, matchState, true, half, awayColor)
					end
				end
			else 
				-- chance failed
				matchState = "AChance Missed"
				if not quickRun then
					SFX.Miss:Play()
					RSSSimulation.newEvent(clock, matchState, true, half, awayColor)
				end
			end
		end
		
		if beginAction() then
			wait(dt)
			if gainPossession() == "hPos" then
				incrementClock()
				matchState = "Home Posession"
				if not quickRun then
					RSSSimulation.newEvent(clock, matchState, true, half, homeColor)
				end
				if createBuildup("H", homeTeam) == "HChanceCreated" then
					matchState = "Home Chance"
					HAttempts = HAttempts + 1
				end
			elseif gainPossession() == "aPos" then
				incrementClock()
				matchState = "Away Posession"
				if not quickRun then
					RSSSimulation.newEvent(clock, matchState, true, half, awayColor)
				end
				if createBuildup("A", awayTeam) == "AChanceCreated" then
					matchState = "Away Chance"
					AAttempts = AAttempts + 1
				end
			else
				matchState = "No Posession"
			end
		else
			matchState = "No Posession"
		end
		incrementClock()
	end
	
	-- firstHalf
	while clock < (45 + (FHstoppageTime)) do
		matchProgress()
	end
	
	local function beginSecondHalf()
		resumeBtn.Visible = false
		-- secondHalf
		local SHstoppageTime = RNG:NextNumber(0,6)
		
		matchState = "No Possession"
		while clock < (90 + (SHstoppageTime)) do
			matchProgress()
		end	
		
		if not quickRun then
			SFX.Whistle:Play()
			wait(.1)
			SFX.Whistle:Play()
			wait(.125)
			SFX.Whistle:Play()
		end
		
		clock = 90
		half = 3
		matchState = "FT"
		
		if not quickRun then
			RSSSimulation.newEvent(clock, matchState, true, half)
		
			print("-- " .. homeTeam .. " attempts: " .. tostring(HAttempts))
			print("-- " .. awayTeam .. " attempts: " .. tostring(AAttempts))
			
			kickOffBtn.Visible = true
		
		end
		isPlaying = false
		
		RSSSimulation.updateResults("result")
		
		--- update table
		
		if SB:WaitForChild("HScore").Value > SB:WaitForChild("AScore").Value then
			-- home win
			homeTeam.Table:WaitForChild("Wins").Value = homeTeam.Table.Wins.Value + 1
			homeTeam.Table:WaitForChild("Points").Value = homeTeam.Table.Points.Value + 3
			
			awayTeam.Table:WaitForChild("Losses").Value = awayTeam.Table.Losses.Value + 1
			
		elseif SB:WaitForChild("HScore").Value < SB:WaitForChild("AScore").Value then
			-- away win
			
			awayTeam.Table:WaitForChild("Wins").Value = awayTeam.Table.Wins.Value + 1
			awayTeam.Table:WaitForChild("Points").Value = awayTeam.Table.Points.Value + 3
			
			homeTeam.Table:WaitForChild("Losses").Value = homeTeam.Table.Losses.Value + 1
		else
			-- draw
			
			homeTeam.Table:WaitForChild("Draws").Value = homeTeam.Table.Draws.Value + 1
			homeTeam.Table:WaitForChild("Points").Value = homeTeam.Table.Points.Value + 1
			
			awayTeam.Table:WaitForChild("Draws").Value = awayTeam.Table.Draws.Value + 1
			awayTeam.Table:WaitForChild("Points").Value = awayTeam.Table.Points.Value + 1
		end
		
		return
	end
	
	if not quickRun then
		SFX.Whistle:Play()
		wait(.1)
		SFX.Whistle:Play()
		wait(.125)
		SFX.Whistle:Play()
	end
	
	clock = 45
	half = 2
	matchState = "HT"
	
	if not quickRun then
		RSSSimulation.newEvent(clock, matchState, true, half)
		resumeBtn.Visible = true
	else
		beginSecondHalf()
	end
	
	local buttonClicked = false
	resumeBtn.MouseButton1Click:Connect(function()
		if not buttonClicked then
			buttonClicked = true
			if matchState == "HT" then
				beginSecondHalf()
				resumeBtn.Visible = false
			end
			wait(0.25)
			buttonClicked = false
		end
	end)
	
	return	
end

local matchTemp = player.PlayerGui:WaitForChild("Fixtures"):WaitForChild("Match")

----- FIXTURE FUNCTIONS

function RSSSimulation.flipMatch(FMatch)
	local hT = FMatch:WaitForChild("homeTeam")
	local aT = FMatch:WaitForChild("awayTeam")
	
	hT.Name = "awayTeam"
	aT.Name = "homeTeam"
	
	return FMatch
end

---- RESULTS
local RTable = match:WaitForChild("Results")
local RStorage = RTable:WaitForChild("Storage")

function RSSSimulation.updateResults(message)
	if message == "result" then
		-- new result
		local nR = RStorage:WaitForChild("Result"):Clone()
		
		nR:WaitForChild("homeTeamColor").ImageColor3 = homeTeam.General:WaitForChild("TeamColor").Value.Color
		nR:WaitForChild("homeSTeamColor").ImageColor3 = homeTeam.General:WaitForChild("STeamColor").Value.Color
		nR:WaitForChild("awayTeamColor").ImageColor3 = awayTeam.General:WaitForChild("TeamColor").Value.Color
		nR:WaitForChild("awaySTeamColor").ImageColor3 = awayTeam.General:WaitForChild("STeamColor").Value.Color
		
		nR:WaitForChild("HScore").Text = SB:WaitForChild("HScore").Value
		nR:WaitForChild("AScore").Text = SB:WaitForChild("AScore").Value
		
		nR:WaitForChild("homeTeam").Text = homeTeam.Value
		nR:WaitForChild("awayTeam").Text = awayTeam.Value
		
		nR.Parent = RTable
		nR.Visible = true
	else
		-- new gameweek / start, halfway, end of season
		local nM = RStorage:WaitForChild("Message"):Clone()
		nM.Label.Text = tostring(message)
		nM.Parent = RTable
		nM.Visible = true
	end
end

---- LEAGUE TABLE UI ELEMENTS
local LTable = match:WaitForChild("Table")


function RSSSimulation.updateLeagueTable(teamsList, round)
	--
	
	local leagueTable = {}
	
	for i = 1, #teamsList, 1 do
		leagueTable[i] = {}
	end
	
	for i = 1, #teamsList, 1 do
		local pointer = teamsList[i].General:WaitForChild("Pointer").Value
		
		local LP = pointer.Table:WaitForChild("Position").Value
		local LTName = pointer.Value
		local LTW = pointer.Table:WaitForChild("Wins").Value
		local LTD = pointer.Table:WaitForChild("Draws").Value
		local LTL = pointer.Table:WaitForChild("Losses").Value
		local LTGF = pointer.Table:WaitForChild("GF").Value
		local LTGA = pointer.Table:WaitForChild("GA").Value
		local LTGD = pointer.Table:WaitForChild("GD").Value
		local LTPts = pointer.Table:WaitForChild("Points").Value
		
		local TC = pointer.General:WaitForChild("TeamColor").Value.Color
		local STC = pointer.General:WaitForChild("STeamColor").Value.Color
		
		leagueTable[i] = { Pos = i, Name = LTName, TeamColor = TC, STeamColor = STC, Wins = LTW, Draws = LTD, Losses = LTL, GF = LTGF, GA = LTGA, GD = LTGD, Points = LTPts, LastPos = LP }
	end
	
	local function LTSorter(a,b)
		if a.Points == b.Points then
			if a.GD == b.GD then
				if a.GF == b.GF then
					if a.GA == b.GA then
						return a.Pos > b.Pos
					else
						return a.GA < b.GA
					end
				else
					return a.GF > b.GF
				end
			else
				return a.GD > b.GD
			end
		else
			return a.Points > b.Points
		end
	end
	
	local function TLSorter(a,b)
		if a.Table:WaitForChild("Points").Value == b.Table:WaitForChild("Points").Value then
			if a.Table:WaitForChild("GD").Value == b.Table:WaitForChild("GD").Value then
				if a.Table:WaitForChild("GF").Value == b.Table:WaitForChild("GF").Value then
					if a.Table:WaitForChild("GA").Value == b.Table:WaitForChild("GA").Value then
						return a.Table:WaitForChild("Position").Value > b.Table:WaitForChild("Position").Value
					else
						return a.Table:WaitForChild("GA").Value < b.Table:WaitForChild("GA").Value
					end
				else
					return a.Table:WaitForChild("GF").Value > b.Table:WaitForChild("GF").Value
				end
			else
				return a.Table:WaitForChild("GD").Value > b.Table:WaitForChild("GD").Value
			end
		else
			return a.Table:WaitForChild("Points").Value > b.Table:WaitForChild("Points").Value
		end
	end
	
	table.sort(leagueTable, LTSorter)
	table.sort(teamsList, TLSorter)
	
	local cellInd = 0
	
	local upIcon = "rbxassetid://5643072550"
	local noMoveIcon = "rbxassetid://5643072106"
	local downIcon = "rbxassetid://5643072326"

	
	-- finally display league table
	print("---")
	for i, v in pairs (LTable:GetChildren()) do
		if v:IsA("Frame") and v.Name ~= "Title" then
			cellInd = cellInd + 1
			
			if cellInd > leagueTable[cellInd].LastPos then
				-- move down
				v:WaitForChild("More").Image = downIcon
				v:WaitForChild("More").ImageColor3 = Color3.new(150, 0, 0)
			elseif cellInd < leagueTable[cellInd].LastPos then
				
				-- move up
				v:WaitForChild("More").Image = upIcon
				v:WaitForChild("More").ImageColor3 = Color3.new(0, 150, 0)
			else
				-- no move
				v:WaitForChild("More").Image = noMoveIcon
				v:WaitForChild("More").ImageColor3 = Color3.new(0, 5, 50)
				
				
			end
			
			v:WaitForChild("Pos").Text = cellInd
			v:WaitForChild("TeamColor").ImageColor3 = leagueTable[cellInd].TeamColor
			v:WaitForChild("STeamColor").ImageColor3 = leagueTable[cellInd].STeamColor
			v:WaitForChild("Team").Text = leagueTable[cellInd].Name
			v:WaitForChild("Played").Text = tostring(round)
			
			v:WaitForChild("Won").Text = tostring(leagueTable[cellInd].Wins)
			v:WaitForChild("Drawn").Text = tostring(leagueTable[cellInd].Draws)
			v:WaitForChild("Lost").Text = tostring(leagueTable[cellInd].Losses)
			
			v:WaitForChild("GF").Text = tostring(leagueTable[cellInd].GF)
			v:WaitForChild("GA").Text = tostring(leagueTable[cellInd].GA)
			v:WaitForChild("GD").Text = tostring(leagueTable[cellInd].GD)
			
			v:WaitForChild("Points").Text = tostring(leagueTable[cellInd].Points)
			
			v.Visible = true
		end
	end
	
	-- update team position
	for newPos = 1, #teamsList, 1 do
		teamsList[newPos].Table:WaitForChild("Position").Value = newPos
	end
	
	
	if #leagueTable > 0 then
		return leagueTable[1].Name
	else
		return ""
	end
end

function RSSSimulation.resetLeagueTable(teamsList)
	
	for i, v in pairs (teamsList) do
		v.Table:WaitForChild("Position").Value = 0
		v.Table:WaitForChild("Wins").Value = 0
		v.Table:WaitForChild("Draws").Value = 0
		v.Table:WaitForChild("Losses").Value = 0
		v.Table:WaitForChild("Points").Value = 0
		v.Table:WaitForChild("GD").Value = 0
		v.Table:WaitForChild("GA").Value = 0
		v.Table:WaitForChild("GF").Value = 0
		v.Table:WaitForChild("Form").Value = ""
	end
	
	for i, v in pairs (LTable:GetChildren()) do
		if v:IsA("Frame") and v.Name ~= "Title" then
			v.Visible = false
		end
	end
end


function RSSSimulation.clearResults()
	for i, v in pairs (RTable:GetChildren()) do
		if v:IsA("Frame") and v.Name ~= "Title" then
			v:Destroy()
		end
	end
end
	
function RSSSimulation.generateFixtures(teamsList)
	RSSSimulation.clearResults()
	RSSSimulation.resetLeagueTable(teamsList)
	
	local teams = #teamsList
	local totalRounds = teams - 1
	local matchesPerRound = teams / 2
	local rounds = {}
	
	for i = 1, totalRounds, 1 do
		rounds[i] = {}
	end
	
	
	for round = 0, totalRounds - 1, 1 do
		for rmatch = 0, matchesPerRound - 1, 1 do
			local homeI = (round + rmatch) % (teams - 1);
			local awayI = (teams - 1 - rmatch + round) % (teams - 1);
			
			-- last team stays in the same place while others rotate around it 
			if (rmatch) == 0 then
				awayI = (teams - 1);
			end
						
			
			local nM = matchTemp:Clone()
			nM.Parent = player.PlayerGui.Fixtures
			nM.Value = tostring(round + 1) .. "." .. tostring(rmatch + 1)
			nM.Name = tostring(round + 1) .. "." .. tostring(rmatch + 1)
			
			local HTm = teamsList[homeI + 1]
			local HTmC = HTm:Clone()
			HTmC.General:WaitForChild("Pointer").Value = HTm
			HTmC.Name = "homeTeam"
			
			local ATm = teamsList[awayI + 1]
			local ATmC = ATm:Clone()
			ATmC.General:WaitForChild("Pointer").Value = ATm
			ATmC.Name = "awayTeam"
			
			HTmC.Parent = nM
			ATmC.Parent = nM
			
			rounds[round + 1][rmatch + 1] = nM
		end
	end
	
	-- interleave so that home and away games are fairly evenly dispersed
	
	local interleaved = {}
	for x = 1, totalRounds, 1 do
		interleaved[x] = {}
	end
	
	local even = 0
	local odd = teams / 2
	
	for p = 0, #rounds - 1, 1 do
		
		if (p % 2 == 0) then
			even = even + 1
			interleaved[p + 1] = rounds[even]
		else
			odd = odd + 1
			interleaved[p + 1] = rounds[odd]
		end
	end
	
	rounds = interleaved
	
	-- last team can't be away for every game so flip them to home on odd rounds
	
	for round = 0, #rounds - 1, 1 do
		if (round % 2 == 1) then
			rounds[round + 1][1] = RSSSimulation.flipMatch(rounds[round + 1][1])
		end
	end
	-- change daylight
	local lighting = game:GetService("Lighting")
	
	-- simulate and display the matches
	
	local leader = RSSSimulation.updateLeagueTable(teamsList, 0)
	
	wait(1)

	RSSSimulation.updateResults("START OF LEAGUE SEASON")
	wait(1.5)
	
	for r = 1, #rounds, 1 do
		SFX.Whistle:Play()
		RSSSimulation.updateResults("ROUND " .. tostring(r))
		
		for m = 1, #rounds[r], 1 do
			lighting.ClockTime = 15 + ((m / #rounds[r]) * 5)
			local newMatch = rounds[r][m]
			RSSSimulation.startGame(newMatch, true)			
		end		
		wait(0.025)
		
		leader = RSSSimulation.updateLeagueTable(teamsList, r)
		print(leader)
	end
	
	-- second half, mirror of first-half fixtures
	
	SFX.Whistle:Play()
	RSSSimulation.updateResults("HALFWAY INTO LEAGUE SEASON")
	RSSSimulation.updateResults(string.upper(leader) .. " LEAD THE TABLE SO FAR!")
	wait(5)
	
	local rc = #rounds
	for sr = 1, #rounds, 1 do
		SFX.Whistle:Play()
		rc = #rounds + sr
		RSSSimulation.updateResults("ROUND " .. tostring(rc))
		
		for sm = 1, #rounds[sr], 1 do
			lighting.ClockTime = 15 + ((sm / #rounds[sr]) * 5)
			local newSMatch = RSSSimulation.flipMatch(rounds[sr][sm])
			RSSSimulation.startGame(newSMatch, true)			
		end
		wait(0.025)
		
		leader = RSSSimulation.updateLeagueTable(teamsList, rc)
	end
	
	SFX.Whistle:Play()
	SFX.Goal:Play()
	RSSSimulation.updateResults(string.upper(leader) .. " CHAMPIONS!")
	RSSSimulation.updateResults("END OF LEAGUE SEASON")
	
	finishSeason.Visible = true	
	
	return
end

return RSSSimulation
