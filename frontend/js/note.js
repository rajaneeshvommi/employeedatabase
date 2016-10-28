/**
 * Created by root on 2015-12-02.
 */


angular.module('LoginApp', [])
    .controller('Login', function($scope, $http) {

        $scope.Check = function (login){
            if ( login.username == "admin" && login.password == "admin"){
                //alert ("Login successful");
                window.location = "home.html";

            }
            else{

                alert ("Login failed");

            }

        }
    });

angular.module('todoApp', [])
    .controller('LoginCtrl', function($scope, $http) {

        $scope.CheckLogin = function (login){
            $http.put('/api/login', login).then(function(response) {
                refreshAllNotes();

                alert("New employee added!");
                window.location.href ="home.html";
            });
        }

        function refreshAllNotes() {
            $http.get('/api/login').then(function(response) {
                $scope.login = response.data;
            });
        }
    });
