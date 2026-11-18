$logFile = "performance/jmeter/docker_stats_e8_e9.csv"
"Timestamp,Container,CPU_Perc,Mem_Usage,Mem_Limit,Mem_Perc,Net_In,Net_Out,Block_In,Block_Out,PIDs" | Out-File -FilePath $logFile -Encoding utf8

$stopFlag = "performance/jmeter/stop_stats.flag"
if (Test-Path $stopFlag) { Remove-Item $stopFlag -Force }

while (-not (Test-Path $stopFlag)) {
    $now = (Get-Date).ToString("yyyy-MM-dd HH:mm:ss")
    $stats = docker stats --no-stream --format "{{.Name}},{{.CPUPerc}},{{.MemUsage}},{{.MemPerc}},{{.NetIO}},{{.BlockIO}},{{.PIDs}}" localspot-backend localspot-postgres
    foreach ($line in $stats) {
        if ($line.Trim() -ne "") {
            $parts = $line.Split(",")
            $container = $parts[0]
            $cpu = $parts[1].Replace("%","").Trim()
            $memParts = $parts[2].Split("/")
            $memUsage = $memParts[0].Trim()
            $memLimit = $memParts[1].Trim()
            $memPerc = $parts[3].Replace("%","").Trim()
            $netParts = $parts[4].Split("/")
            $netIn = $netParts[0].Trim()
            $netOut = $netParts[1].Trim()
            $blockParts = $parts[5].Split("/")
            $blockIn = $blockParts[0].Trim()
            $blockOut = $blockParts[1].Trim()
            $pids = $parts[6].Trim()
            
            "$now,$container,$cpu,$memUsage,$memLimit,$memPerc,$netIn,$netOut,$blockIn,$blockOut,$pids" | Out-File -FilePath $logFile -Append -Encoding utf8
        }
    }
    Start-Sleep -Seconds 5
}
