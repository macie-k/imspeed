$path = Read-Host -Prompt 'Path to SDK root directory (not bin)'
[System.Environment]::SetEnvironmentVariable("JAVA_JDK", $path, [System.EnvironmentVariableTarget]::User)
if($?){
	Write-Host "JAVA_JDK Was successfully set to:  $path" -ForegroundColor green
} else {
	Write-Host "Error, check if you have permissions" -ForegroundColor red
}
Start-Sleep -s 1
Remove-Item $MyINvocation.InvocationName