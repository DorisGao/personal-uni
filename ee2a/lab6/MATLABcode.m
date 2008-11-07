% EE2A2RS232Comms
% Receive a data string from RS232 - assumed to have the TAOS TSL3301 sensor connected
clear
PH = serial('COM1');    % Set up the characteristics of the RS232 port (9600 baud, 8-bits, etc)
pause(2);               % Wait for Windows to catch up
% If necessary add =
set(PH,'BaudRate',4800,'DataBits',8,'Parity','none','StopBits',1,'FlowControl','hardware','Terminator','LF','Timeout',120);
fprintf('starting matlab code')
fopen(PH);  % Open the RS232 Port so we can talk to it
% Note - Some early versions of MATLAB crash at this stage as the serial port is in use by something else
% Close down all other programs and try again - reboot if necessary
pause(2);   % wait for Windows to catch up
PixelNumber = 1:102;     % Make an array of pixel numbers for display purposes
% Initialise an array to be filled with values from the sensor
PixelValues = zeros(1,102); 
% Normally we would run this loop continuously, in this case we will only do this a few times
while 1,
%for i=1:300,
% Infinate loop
	RecoveredData = fscanf(PH);	% Get some data from the sensor
	%fprintf('%s %d\n',RecoveredData,length(RecoveredData));
% Wait for start-of-frame message from PIC
    if ~isempty(findstr(RecoveredData,'Start')),
        disp('---');
% get 102 values
        for PixelIndex = 1:102,
            RecoveredValue = str2double(fscanf(PH));
            PixelValues(PixelIndex) = RecoveredValue;
        end
    % Plot raw data in a report-ready format
        Figure1Handle = figure(1);
        plot(PixelNumber,PixelValues,'k','linewidth',1.5);
        title('Raw Pixel Data','FontWeight','demi','FontSize', 11)
        ylabel('Pixel Value','FontWeight','demi','FontSize', 11)
        xlabel('Pixel Number','FontWeight','demi','FontSize', 11)
        set(gca,'LineWidth',2,'FontWeight','demi')
        set(Figure1Handle,'Color','w')	
    % Set an adaptive threshold based on adaptive data - think about overflow a PIC 'int' format
        Threshold = min(PixelValues)/2 + max(PixelValues)/2;
    % Now look for those pixels with values greater than the threshold
        DetectedPixels = PixelValues > Threshold;
    % Plot out detected pixels
        Figure2Handle = figure(2);
        plot(PixelNumber,PixelValues,'k','linewidth',1.5);
    % PLot the raw data
        hold on; plot(PixelNumber,300*DetectedPixels,'b','linewidth',1.5); hold off;
    % Overlay detected data
        line([1 102],[Threshold Threshold], 'Color','r')
    % Show the threshold value
        title('Raw Pixel Data','FontWeight','demi','FontSize', 11)
        ylabel('Pixel Value','FontWeight','demi','FontSize', 11)
        xlabel('Pixel Number','FontWeight','demi','FontSize', 11)
        set(gca,'LineWidth',2,'FontWeight','demi')
        set(Figure1Handle,'Color','w')	
        MeanValue = sum(DetectedPixels.*PixelNumber)./sum(DetectedPixels);
    % Calculate mean suitable for the PIC
        fprintf('Mean position of line = %f\n',MeanValue)
        pause(0.5)	% Wait for Windows to catch up
    end
end
fclose(PH);
delete(PH);
clear PH;


	                                                                                    