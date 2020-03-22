set-executionpolicy remotesigned

echo try { >> setup.ps1
echo 	$java = (Get-Command java -erroraction 'silentlycontinue' ^| Select-Object -ExpandProperty Source).toString().TrimEnd("\bin\java.exe") >> setup.ps1
echo } catch { >> setup.ps1
echo 	Write-Host "No JAVA_HOME set" -f red >> setup.ps1
echo 	$err = $true >> setup.ps1
echo } >> setup.ps1
echo Write-Host "" >> setup.ps1
echo if(!$err){ >> setup.ps1
echo 	Write-Host "Is:" -nonewline; Write-Host " $java " -f yellow -nonewline; $path = Read-Host -Prompt "path to JDK? [y/n]" >> setup.ps1
echo 	if($path -eq "y"){ >> setup.ps1
echo 		$path = $java; >> setup.ps1
echo 	} else { >> setup.ps1
echo 		$path = Read-Host -Prompt 'Path to JDK root directory' >> setup.ps1
echo 	} >> setup.ps1
echo } else { >> setup.ps1
echo 	$path = Read-Host -Prompt 'Path to JDK root directory' >> setup.ps1
echo } >> setup.ps1
echo Write-Host "" >> setup.ps1
echo [System.Environment]::SetEnvironmentVariable("JAVA_JDK", $path, [System.EnvironmentVariableTarget]::User) >> setup.ps1
echo if($?){ >> setup.ps1
echo 	Write-Host "JAVA_JDK" -f yellow -nonewline; Write-Host " Was successfully set to:  " -nonewline; Write-Host "$path" -ForegroundColor green >> setup.ps1
echo 	Remove-Item -LiteralPath $MyInvocation.MyCommand.Path -Force >> setup.ps1
echo } else { >> setup.ps1
echo 	Write-Host "Error, check if you have permissions" -ForegroundColor red >> setup.ps1
echo } >> setup.ps1
echo Start-Sleep -s 3 >> setup.ps1

del "%~f0"