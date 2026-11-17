/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 88.86780518659077, "KoPercent": 11.132194813409235};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.8438751844823951, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "GET_02_Lugares"], "isController": false}, {"data": [0.24295774647887325, 500, 1500, "E7_4_Delete_Favorito"], "isController": false}, {"data": [0.9108391608391608, 500, 1500, "E7_3_Read_Favoritos"], "isController": false}, {"data": [1.0, 500, 1500, "E4_4_Consulta_Final"], "isController": false}, {"data": [0.9991666666666666, 500, 1500, "POST_05_Login_Stress"], "isController": false}, {"data": [1.0, 500, 1500, "GET_06_Consultas_Criticas"], "isController": false}, {"data": [0.5983050847457627, 500, 1500, "E7_1_Login"], "isController": false}, {"data": [1.0, 500, 1500, "E4_2_Consulta"], "isController": false}, {"data": [0.17881944444444445, 500, 1500, "E7_2_Create_Favorito"], "isController": false}, {"data": [1.0, 500, 1500, "E4_1_Login"], "isController": false}, {"data": [0.964, 500, 1500, "POST_01_Login"], "isController": false}, {"data": [0.1, 500, 1500, "POST_03_Register"], "isController": false}, {"data": [0.005, 500, 1500, "E4_3_Registrar_Favorito"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 4743, 528, 11.132194813409235, 204.1528568416616, 2, 8493, 23.0, 560.6000000000004, 796.8000000000002, 1601.5600000000004, 39.96461071789687, 55.68688022676525, 10.784704338873441], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["GET_02_Lugares", 800, 0, 0.0, 21.625000000000036, 11, 98, 21.0, 25.0, 26.0, 32.0, 53.05391604217787, 259.10413488958153, 10.6729557662975], "isController": false}, {"data": ["E7_4_Delete_Favorito", 284, 206, 72.53521126760563, 410.12323943661954, 30, 1905, 278.0, 868.5, 1237.0, 1657.2999999999981, 18.40451040114056, 9.559123578348778, 8.303597466139589], "isController": false}, {"data": ["E7_3_Read_Favoritos", 286, 0, 0.0, 321.6433566433565, 22, 1157, 261.0, 630.5000000000002, 765.65, 1059.3199999999997, 17.79824506814363, 11.100748627014749, 7.612921230319249], "isController": false}, {"data": ["E4_4_Consulta_Final", 100, 0, 0.0, 13.91, 6, 109, 11.0, 25.80000000000001, 31.849999999999966, 108.29999999999964, 10.766580534022395, 12.20703125, 2.029248088931955], "isController": false}, {"data": ["POST_05_Login_Stress", 600, 0, 0.0, 223.7383333333334, 107, 581, 218.0, 321.0, 358.0, 401.8600000000001, 19.85965841387528, 15.592934926519264, 5.275221766185622], "isController": false}, {"data": ["GET_06_Consultas_Criticas", 1500, 0, 0.0, 7.557999999999994, 2, 60, 7.0, 10.0, 12.0, 20.99000000000001, 74.74213961831681, 38.46592536997359, 15.546948963575664], "isController": false}, {"data": ["E7_1_Login", 295, 0, 0.0, 750.2169491525426, 266, 1902, 671.0, 1181.4, 1456.8, 1750.9600000000005, 16.479526283447854, 12.939003058488353, 4.377374169040836], "isController": false}, {"data": ["E4_2_Consulta", 100, 0, 0.0, 18.110000000000003, 9, 57, 16.0, 28.900000000000006, 30.94999999999999, 56.87999999999994, 9.91866693116445, 26.540182999404877, 1.9856706258678831], "isController": false}, {"data": ["E7_2_Create_Favorito", 288, 223, 77.43055555555556, 490.54166666666634, 22, 2201, 422.5, 959.5000000000001, 1082.6000000000001, 1787.7400000000032, 17.53958587088916, 9.97848231957978, 7.879110840438488], "isController": false}, {"data": ["E4_1_Login", 100, 0, 0.0, 131.59, 83, 207, 127.5, 169.9, 179.84999999999997, 206.92999999999995, 9.651578033008397, 7.577996814979248, 2.563700415017855], "isController": false}, {"data": ["POST_01_Login", 250, 0, 0.0, 240.98399999999978, 74, 744, 177.5, 464.9, 572.3499999999999, 733.96, 24.85830764641543, 19.517655613005864, 6.602987968579099], "isController": false}, {"data": ["POST_03_Register", 40, 0, 0.0, 3866.1749999999993, 1229, 8493, 3674.5, 6539.4, 8028.899999999995, 8493.0, 2.683483161143164, 1.6370164489131893, 0.8484812114249295], "isController": false}, {"data": ["E4_3_Registrar_Favorito", 100, 99, 99.0, 31.080000000000016, 6, 961, 12.0, 23.0, 32.94999999999999, 959.1399999999991, 9.949258780220873, 5.701488813302159, 4.469393592677346], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["500", 130, 24.62121212121212, 2.7408812987560616], "isController": false}, {"data": ["404", 145, 27.46212121212121, 3.057136833227915], "isController": false}, {"data": ["409", 253, 47.916666666666664, 5.334176681425259], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 4743, 528, "409", 253, "404", 145, "500", 130, "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": ["E7_4_Delete_Favorito", 284, 206, "404", 145, "500", 61, "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["E7_2_Create_Favorito", 288, 223, "409", 155, "500", 68, "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["E4_3_Registrar_Favorito", 100, 99, "409", 98, "500", 1, "", "", "", "", "", ""], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
